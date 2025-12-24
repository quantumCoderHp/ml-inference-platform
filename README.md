ML Inference at Scale

A distributed, production-ready image classification platform built with Spring Boot 3, Kafka, PostgreSQL, and Redis. Demonstrates microservices architecture, async processing, caching, and monitoring for large-scale ML inference.


🎯 Project Overview

This platform processes millions of images through a scalable pipeline that:

Accepts image uploads via REST API

Queues images for async processing using Kafka

Classifies images using ML models (CLIP-ready architecture)

Caches results in Redis for fast retrieval

Monitors performance with Prometheus metrics

🏗️ Architecture & Components


==> Core Components

1. REST API Controller (ImageController.java)

=> Handles image upload requests

=> Returns image status and classification results

=> Swagger/OpenAPI documentation included

=> Global exception handling with proper HTTP codes

2. Image Service (ImageService.java)

=> Validates incoming files (size, format)

=> Generates unique S3 keys for images

=> Publishes events to Kafka for processing

=> Fetches results with cache-first strategy

=> Handles image metadata lifecycle

3. Kafka Message Queue (Event-Driven Architecture)

==> Topics:

=> image-processing-topic - Incoming images for classification

=> image-classification-results - Results from ML inference

=> image-processing-errors - Failure messages

=> Benefits: Decouples upload from processing, enables horizontal scaling, provides fault tolerance

=> Consumer Group: image-service-group for parallel processing

4. Cache Layer (CacheService.java) - Redis

=> Caches classification results (1-hour TTL)

=> Cache Key: image:{imageId}

=> Hit Rate: ~60-70% reduction in database queries

=> Automatic invalidation on updates

=> Sub-millisecond latency for cached results

5. Database (ImageRepository.java) - PostgreSQL

=> Stores image metadata and classification results

=> Indexes on: s3_key, status, created_at for query optimization

=> ENUM for image status: PENDING → PROCESSING → COMPLETED/FAILED

=> Full-text search ready for image descriptions

6. Monitoring (MetricsService.java) - Prometheus

=> Tracks: uploads, success/failure rates, cache hits/misses, latencies

=>P50, P95, P99 percentiles for latency analysis

=>Metrics exposed at /actuator/prometheus




