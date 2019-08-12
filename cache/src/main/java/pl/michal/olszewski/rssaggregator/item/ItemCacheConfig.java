package pl.michal.olszewski.rssaggregator.item;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
class ItemCacheConfig {

  @Bean(name = "itemCache")
  @Primary
  public Cache itemCache(MeterRegistry registry, ItemFinder finder) {
    Cache<String, ItemDTO> cache = Caffeine.newBuilder()
        .expireAfterAccess(2, TimeUnit.DAYS)
        .maximumSize(30000)
        .build();
    finder.findAllOrderByPublishedDateBlocking(200, 0)
        .forEach(item -> cache.put(item.getLink(), ItemToDtoMapper.mapItemToItemDTO(item)));
    registry.gauge("itemCache", cache, Cache::estimatedSize);
    return cache;
  }
}
