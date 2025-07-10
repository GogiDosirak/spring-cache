package com.sprint.springcache.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

@Service
public class MemoryMonitoringService {

  private final CacheManager cacheManager;

  public MemoryMonitoringService(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public Map<String, Object> getMemoryStats() {
    Map<String, Object> stats = new HashMap<>();

    // JVM 메모리 정보
    Runtime runtime = Runtime.getRuntime();
    long usedMemory = runtime.totalMemory() - runtime.freeMemory();
    long maxMemory = runtime.maxMemory();

    stats.put("jvm", Map.of(
        "usedMemoryMB", usedMemory / 1024 / 1024,
        "maxMemoryMB", maxMemory / 1024 / 1024,
        "usagePercent", String.format("%.1f%%", (double) usedMemory / maxMemory * 100)
    ));

    // 캐시별 크기 정보
    Map<String, Long> cacheSizes = new HashMap<>();
    cacheManager.getCacheNames().forEach(cacheName -> {
      Cache cache = cacheManager.getCache(cacheName);
      if (cache instanceof CaffeineCache) {
        CaffeineCache caffeineCache = (CaffeineCache) cache;
        cacheSizes.put(cacheName, caffeineCache.getNativeCache().estimatedSize());
      }
    });
    stats.put("cacheSizes", cacheSizes);

    return stats;
  }
}