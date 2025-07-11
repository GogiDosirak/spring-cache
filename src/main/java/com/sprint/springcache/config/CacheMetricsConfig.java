package com.sprint.springcache.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Configuration;

// Prometheus로 메트릭을 scrape하기 위함
@Configuration
public class CacheMetricsConfig {
  private final CacheManager cacheManager;
  private final MeterRegistry meterRegistry;

  public CacheMetricsConfig(CacheManager cacheManager, MeterRegistry meterRegistry) {
    this.cacheManager = cacheManager;
    this.meterRegistry = meterRegistry;
  }

  @PostConstruct
  public void bindCacheMetrics() {
    cacheManager.getCacheNames().forEach(cacheName -> {
      CaffeineCache cache = (CaffeineCache) cacheManager.getCache(cacheName);
      if (cache != null) {
        // CaffeineCacheMetrice와 meterRegistry를 연결
        CaffeineCacheMetrics.monitor(meterRegistry, cache.getNativeCache(), cacheName);
      }
    });
  }

}
