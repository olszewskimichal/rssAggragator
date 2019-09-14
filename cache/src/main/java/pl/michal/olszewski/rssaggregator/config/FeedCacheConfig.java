package pl.michal.olszewski.rssaggregator.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class FeedCacheConfig {

  @Bean(name = "feedCache")
  Cache feedCacheProd(MeterRegistry registry) {
    Cache cache = Caffeine.newBuilder()
        .expireAfterAccess(24, TimeUnit.HOURS)
        .maximumSize(30000)
        .build();
    registry.gauge("feedCache", cache, Cache::estimatedSize);
    return cache;
  }
}
