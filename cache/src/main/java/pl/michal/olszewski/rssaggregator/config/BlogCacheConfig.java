package pl.michal.olszewski.rssaggregator.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class BlogCacheConfig {

  @Bean(name = "blogCache")
  Cache blogByIdCacheProd(MeterRegistry registry) {
    Cache cache = Caffeine.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .maximumSize(30000)
        .build();
    registry.gauge("blogByIdCache", cache, Cache::estimatedSize);
    return cache;
  }
}
