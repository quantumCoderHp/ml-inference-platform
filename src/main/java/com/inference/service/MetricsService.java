package com.inference.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;

    public void recordImageUpload() {
        Counter.builder("images.uploaded")
                .description("Total images uploaded")
                .register(meterRegistry)
                .increment();
    }

    public void recordSuccessfulProcessing() {
        Counter.builder("images.processed.success")
                .description("Successfully processed images")
                .register(meterRegistry)
                .increment();
    }

    public void recordFailedProcessing() {
        Counter.builder("images.processed.failed")
                .description("Failed image processing")
                .register(meterRegistry)
                .increment();
    }

    public void recordCacheHit() {
        Counter.builder("cache.hits")
                .description("Cache hits")
                .register(meterRegistry)
                .increment();
    }

    public void recordCacheMiss() {
        Counter.builder("cache.misses")
                .description("Cache misses")
                .register(meterRegistry)
                .increment();
    }

    public void recordProcessingTime(long durationMs) {
        Timer.builder("image.processing.duration")
                .description("Time to process image")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }

}