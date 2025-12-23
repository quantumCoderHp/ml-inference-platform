package com.inference.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse {
    private Long id;
    private String imageUrl;
    private String status;
    private String classificationResult;
    private Double confidenceScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
