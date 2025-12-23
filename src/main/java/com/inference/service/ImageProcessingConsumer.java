package com.inference.service;

import com.inference.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingConsumer {

    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final MetricsService metricsService;

    // Listens for classification results from inference worker
    @KafkaListener(topics = "image-classification-results", groupId = "image-service-group")
    public void consumeClassificationResult(String message) {
        try {
            log.info("Received classification result: {}", message);

            // Message format: "imageId:result:confidence"
            String[] parts = message.split(":");
            if (parts.length >= 3) {
                Long imageId = Long.parseLong(parts[0]);
                String result = parts[1];
                Double confidence = Double.parseDouble(parts[2]);

                imageService.updateImageResult(imageId, result, confidence);
                metricsService.recordSuccessfulProcessing();

            } else {
                log.warn("Invalid message format: {}", message);
            }

        } catch (Exception e) {
            log.error("Error processing classification result", e);
            metricsService.recordFailedProcessing();
        }
    }

    // Listens for processing errors
    @KafkaListener(topics = "image-processing-errors", groupId = "image-service-group")
    public void consumeProcessingError(String message) {
        try {
            log.warn("Received processing error: {}", message);

            // Message format: "imageId:errorMessage"
            String[] parts = message.split(":", 2);
            if (parts.length >= 2) {
                Long imageId = Long.parseLong(parts[0]);
                String errorMessage = parts[1];

                imageService.markImageFailed(imageId, errorMessage);
            }

        } catch (Exception e) {
            log.error("Error processing error message", e);
        }
    }

}