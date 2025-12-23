package com.inference.controller;

import com.inference.model.ImageEntity;
import com.inference.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Image Management", description = "Upload and retrieve images for classification")

public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload an image for CLIP classification",
            description = "Accepts image file and queues for processing")
    @ApiResponse(responseCode = "201", description = "Image uploaded successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageEntity.class)))
    @ApiResponse(responseCode = "400", description = "Invalid file")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<ImageResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Received image upload request: {}", file.getOriginalFilename());

            ImageEntity image = imageService.uploadImage(file);

            ImageResponse response = ImageResponse.builder()
                    .id(image.getId())
                    .imageUrl(image.getImageUrl())
                    .status(image.getStatus().toString())
                    .createdAt(image.getCreatedAt())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid image upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error uploading image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get image by ID", description = "Retrieve image status and classification result")
    @ApiResponse(responseCode = "200", description = "Image found")
    @ApiResponse(responseCode = "404", description = "Image not found")
    public ResponseEntity<ImageResponse> getImage(@PathVariable Long id) {
        try {
            log.debug("Fetching image: {}", id);

            ImageEntity image = imageService.getImageWithCache(id);

            ImageResponse response = ImageResponse.builder()
                    .id(image.getId())
                    .imageUrl(image.getImageUrl())
                    .status(image.getStatus().toString())
                    .classificationResult(image.getClassificationResult())
                    .confidenceScore(image.getConfidenceScore())
                    .createdAt(image.getCreatedAt())
                    .updatedAt(image.getUpdatedAt())
                    .build();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Image not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Image Service is running");
    }

}