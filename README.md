ML Inference at Scale

A distributed, production-ready image classification platform built with Spring Boot 3, Kafka, PostgreSQL, and Redis. Demonstrates microservices architecture, async processing, caching, and monitoring for large-scale ML inference.

🎯 Project Overview

This platform processes millions of images through a scalable pipeline that:

Accepts image uploads via REST API

Queues images for async processing using Kafka

Classifies images using ML models (CLIP-ready architecture)

Caches results in Redis for fast retrieval

Monitors performance with Prometheus metrics


