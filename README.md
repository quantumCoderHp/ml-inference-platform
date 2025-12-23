ML Inference at Scale
A distributed, production-ready image classification platform built with Spring Boot 3, Kafka, PostgreSQL, and Redis. Demonstrates microservices architecture, async processing, caching, and monitoring for large-scale ML inference.

🎯 Project Overview
This platform processes millions of images through a scalable pipeline that:

Accepts image uploads via REST API
Queues images for async processing using Kafka
Classifies images using ML models (CLIP-ready architecture)
Caches results in Redis for fast retrieval
Monitors performance with Prometheus metrics

Perfect for: Google L4/L5 interviews, senior backend role demonstrations, microservices portfolio.

🏗️ Architecture
┌─────────────────────────────────────────────────┐
│            API Gateway (Spring Boot)             │
│     (Rate Limiting, Validation, Auth Ready)      │
└──────────────────┬──────────────────────────────┘
                   │
        ┌──────────┼──────────┐
        │          │          │
┌───────▼────┐ ┌──┴──────┐ ┌─┴────────┐
│   Image    │ │ Kafka   │ │ Cache    │
│  Service   │ │ Topics  │ │ (Redis)  │
│ (Upload)   │ │         │ │ 1hr TTL  │
└────┬───────┘ └────┬────┘ └──┬───────┘
     │              │         │
     └──────────────┼─────────┘
                    │
         ┌──────────▼──────────┐
         │ Inference Consumer  │
         │ (Kafka Listener)    │
         │ (Ready for ML)      │
         └──────────┬──────────┘
                    │
         ┌──────────▼──────────┐
         │   PostgreSQL DB     │
         │  (Full-text index)  │
         └─────────────────────┘

🚀 Key Features
Backend Architecture
✅ Microservices Design - Stateless, horizontally scalable services
✅ Event-Driven - Kafka for async image processing pipeline
✅ Caching Layer - Redis with 1-hour TTL for hot data
✅ Database Optimization - PostgreSQL with indexes on frequently queried columns
✅ Error Handling - Global exception handler with proper HTTP status codes
✅ Monitoring - Prometheus metrics for all operations
Production-Ready
✅ Retry Logic - Exponential backoff for Kafka failures
✅ Health Checks - Spring Actuator endpoints
✅ Structured Logging - SLF4J with contextual information
✅ API Documentation - Swagger/OpenAPI 3.0
✅ Docker Support - Containerized with Docker Compose
