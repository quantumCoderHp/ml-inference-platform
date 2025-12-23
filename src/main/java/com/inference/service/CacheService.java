package com.inference.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inference.model.ImageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
@Service
@RequiredArgsConstructor
@Slf4j

public class CacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long CACHE_TTL_SECONDS = 3600; // 1 hour
    private static final String CACHE_KEY_PREFIX = "image:";

    public void cacheResult(Long imageId, ImageEntity image) {
        try {
            String cacheKey = CACHE_KEY_PREFIX + imageId;
            String jsonValue = objectMapper.writeValueAsString(image);

            redisTemplate.opsForValue()
                    .set(cacheKey, jsonValue, Duration.ofSeconds(CACHE_TTL_SECONDS));

            log.debug("Cached image {}", imageId);

        } catch (Exception e) {
            log.warn("Failed to cache image {}", imageId, e);
        }
    }

    public String getFromCache(Long imageId) {
        try {
            String cacheKey = CACHE_KEY_PREFIX + imageId;
            return redisTemplate.opsForValue().get(cacheKey);

        } catch (Exception e) {
            log.warn("Failed to get from cache for image {}", imageId, e);
            return null;
        }
    }

    public void invalidateCache(Long imageId) {
        try {
            String cacheKey = CACHE_KEY_PREFIX + imageId;
            redisTemplate.delete(cacheKey);
            log.debug("Invalidated cache for image {}", imageId);

        } catch (Exception e) {
            log.warn("Failed to invalidate cache for image {}", imageId, e);
        }
    }

    public void clearAllCache() {
        try {
            redisTemplate.delete(redisTemplate.keys(CACHE_KEY_PREFIX + "*"));
            log.info("Cleared all image cache");

        } catch (Exception e) {
            log.error("Failed to clear all cache", e);
        }
    }
}
