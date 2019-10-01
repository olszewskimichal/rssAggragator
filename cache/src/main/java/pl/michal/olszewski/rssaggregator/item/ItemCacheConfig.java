package pl.michal.olszewski.rssaggregator.item;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class ItemCacheConfig {

  @Bean(name = "itemCache")
  Cache<BlogItemLink, ItemDTO> itemCache(MeterRegistry registry, ItemFinder finder) {
    Cache<BlogItemLink, ItemDTO> cache = Caffeine.newBuilder()
        .maximumSize(30000)
        .build();
    finder.findAllOrderByPublishedDateBlocking(200, 0)
        .forEach(item -> cache.put(new BlogItemLink(item.getBlogId(), item.getLink()), ItemToDtoMapper.mapItemToItemDTO(item)));
    registry.gauge("itemCache", cache, Cache::estimatedSize);
    return cache;
  }
}
