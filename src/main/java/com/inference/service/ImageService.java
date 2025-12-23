package com.inference.service;

import com.inference.model.ImageEntity;
import com.inference.model.ImageStatus;
import com.inference.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CacheService cacheService;
    private final MetricsService metricsService;

    private static final String KAFKA_TOPIC = "image-processing-topic";

    public ImageEntity uploadImage(MultipartFile file) throws Exception {
        long startTime = System.currentTimeMillis();

        log.info("Starting image upload: {}", file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Generate unique S3 key
        String s3Key = generateS3Key(file.getOriginalFilename());
        String imageUrl = "https://cdn.example.com/" + s3Key;

        // Create and save entity
        ImageEntity image = ImageEntity.builder()
                .imageUrl(imageUrl)
                .s3Key(s3Key)
                .status(ImageStatus.PENDING)
                .build();

        ImageEntity savedImage = imageRepository.save(image);
        log.info("Image saved with ID: {}", savedImage.getId());

        // Send to Kafka for processing
        sendToKafkaForProcessing(savedImage.getId(), s3Key);

        metricsService.recordImageUpload();

        long duration = System.currentTimeMillis() - startTime;
        log.info("Image upload completed in {}ms", duration);

        return savedImage;
    }

//    @Retry(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    private void sendToKafkaForProcessing(Long imageId, String s3Key) {
        try {
            String message = imageId + ":" + s3Key;
            kafkaTemplate.send(KAFKA_TOPIC, imageId.toString(), message);
            log.info("Sent to Kafka - ImageID: {}, S3Key: {}", imageId, s3Key);
        } catch (Exception e) {
            log.error("Error sending to Kafka", e);
            throw e;
        }
    }

    public ImageEntity getImage(Long id) {
        log.debug("Fetching image with ID: {}", id);

        return imageRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Image not found: {}", id);
                    return new RuntimeException("Image not found with ID: " + id);
                });
    }

    public ImageEntity getImageWithCache(Long id) {
        // Try cache first
        String cached = cacheService.getFromCache(id);
        if (cached != null) {
            log.debug("Cache hit for image {}", id);
            metricsService.recordCacheHit();
            // Parse cached JSON and return
            return getImage(id);
        }

        // Cache miss - fetch from DB
        metricsService.recordCacheMiss();
        ImageEntity image = getImage(id);

        // Cache for future requests
        if (image.getStatus() == ImageStatus.COMPLETED) {
            cacheService.cacheResult(id, image);
        }

        return image;
    }

    public void updateImageResult(Long id, String result, Double confidence) {
        log.info("Updating image {} with classification result", id);

        ImageEntity image = getImage(id);
        image.setStatus(ImageStatus.COMPLETED);
        image.setClassificationResult(result);
        image.setConfidenceScore(confidence);

        ImageEntity updated = imageRepository.save(image);

        // Cache the result
        cacheService.cacheResult(id, updated);

        log.info("Image {} updated successfully", id);
        metricsService.recordSuccessfulProcessing();
    }

    public void markImageFailed(Long id, String errorMessage) {
        log.error("Marking image {} as failed: {}", id, errorMessage);

        ImageEntity image = getImage(id);
        image.setStatus(ImageStatus.FAILED);
        image.setErrorMessage(errorMessage);

        imageRepository.save(image);
        metricsService.recordFailedProcessing();
    }

    public void markImageProcessing(Long id) {
        ImageEntity image = getImage(id);
        image.setStatus(ImageStatus.PROCESSING);
        imageRepository.save(image);
        log.debug("Marked image {} as PROCESSING", id);
    }

    private String generateS3Key(String originalFileName) {
        return "images/" + UUID.randomUUID() + "-" + originalFileName;
    }
}
