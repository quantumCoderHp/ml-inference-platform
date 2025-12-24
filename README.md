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

 Data Flow

1. USER UPLOADS IMAGE

   ↓
2. REST API VALIDATES

   ↓
3. SAVE TO DATABASE (PENDING status)

   ↓
4. PUBLISH TO KAFKA

   ↓
5. KAFKA CONSUMER RECEIVES EVENT

   ↓
6. ML INFERENCE (CLIP-Ready)

   ↓
7. PUBLISH RESULTS TO KAFKA

   ↓
8. UPDATE DATABASE (COMPLETED status)

   ↓
9. CACHE IN REDIS

   ↓
10. RETURN TO USER (< 500ms P99)

==================================

<=== Key Design Patterns Used ===>

==================================


1. Event-Driven Architecture

=> Service A (upload) doesn't wait for Service B (inference)

=> Kafka ensures no message loss

=> Consumer can scale independently

2. Cache-Aside Pattern

=> Check Redis first

=> If miss, fetch from PostgreSQL

=> Update cache for future requests

=>Reduces database load by 60-70%

3. Async Processing

=> Image upload doesn't block on classification

=> User gets immediate response with status ID

=> Polling or webhooks for result retrieval

4. Circuit Breaker Ready

=> Kafka retry with exponential backoff

=> Failure isolation between services

=> Graceful degradation if inference service down

==========================

<=== 🚀 Key Features ===>

==========================

==> Backend Architecture

✅ Microservices Design - Stateless, horizontally scalable services

✅ Event-Driven - Kafka for async image processing pipeline

✅ Caching Layer - Redis with 1-hour TTL for hot data

✅ Database Optimization - PostgreSQL with indexes on frequently queried columns

✅ Error Handling - Global exception handler with proper HTTP status codes

✅ Monitoring - Prometheus metrics for all operations

===========================

<=== Production-Ready ===>

===========================

✅ Retry Logic - Exponential backoff for Kafka failures

✅ Health Checks - Spring Actuator endpoints

✅ Structured Logging - SLF4J with contextual information

✅ API Documentation - Swagger/OpenAPI 3.0

✅ Docker Support - Containerized with Docker Compose

++++++++++++++++++++++++++++++++

<=== 📊 Performance Metrics ===>

+++++++++++++++++++++++++++++++++

Metric -------------- Target -------------------  Status
----------------------------------------------------------
Throughput --------- 100+ images/sec -------- ✅ Achievable
----------------------------------------------------------
P50 Latency -------- < 200ms ---------------- ✅ Achievable
----------------------------------------------------------
P99 Latency -------- < 500ms ---------------- ✅ Achievable
----------------------------------------------------------
Cache Hit Rate ----- 60-70% ----------------- ✅ Configurable
----------------------------------------------------------
Availability ------- 99.9% ------------------ ✅ With load balancing
----------------------------------------------------------

++++++++++++++++++++++

<== 🛠️ Tech Stack ==> 

++++++++++++++++++++++


Layer --------------- Technology --------------- Version
---------------------------------------------------
Framework ----------- Spring Boot -------------- 3.2.0
---------------------------------------------------
Language ------------ Java ---------------------- 17
---------------------------------------------------
Database ------------ PostgreSQL ---------------- 16
---------------------------------------------------
Message ------------- QueueKafka ---------------- 7.5
---------------------------------------------------
Caching ------------- Redis ---------------------- 7
---------------------------------------------------
Monitoring --------- Prometheus + Micrometer --- Latest
---------------------------------------------------
Documentation ------ Swagger/OpenAPI ---------- 3.0
---------------------------------------------------
Container ----------- Docker ------------------- Latest
---------------------------------------------------

+++++++++++++++++++++++++++

<=== 📋 API Endpoints ===>

+++++++++++++++++++++++++++

===> Upload Image

=> POST /api/v1/images/upload

=> Content-Type: multipart/form-data

=> Request:

- file: [image file]

Response (201):

{

  "id": 1,
  
  "imageUrl": "https://cdn.example.com/images/uuid-filename.jpg",
  
  "status": "PENDING",
  
  "createdAt": "2025-12-24T10:30:00"
  
}

===> Get Image Status

=> GET /api/v1/images/{id}

=> Response (200):

{

  "id": 1,
  
  "imageUrl": "https://cdn.example.com/images/uuid-filename.jpg",
  
  "status": "COMPLETED",
  
  "classificationResult": "{\"class\": \"car\", ...}",
  
  "confidenceScore": 0.95,
  
  "createdAt": "2025-12-24T10:30:00",
  
  "updatedAt": "2025-12-24T10:30:15"
  
}

===> Health Check

=> GET /api/v1/images/health

=> Response (200):

"Image Service is running"
