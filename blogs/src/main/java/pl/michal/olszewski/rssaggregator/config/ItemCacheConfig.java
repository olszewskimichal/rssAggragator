package pl.michal.olszewski.rssaggregator.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
class ItemCacheConfig {

  @Bean(name = "itemCache")
  @Profile({Profiles.PRODUCTION})
  @Primary
  public Cache itemCacheProd(MeterRegistry registry) {
    Cache cache = Caffeine.newBuilder()
        .expireAfterAccess(2, TimeUnit.DAYS)
        .maximumSize(30000)
        .build();
    registry.gauge("itemCache", cache, Cache::estimatedSize);
    return cache;
  }

  @Bean(name = "itemCache")
  @Profile({Profiles.TEST, Profiles.DEVELOPMENT})
  @Primary
  public Cache feedCache() {
    return Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .maximumSize(300000)
        .build();
  }
}
