package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class BlogCacheConfig {

  @Bean(name = "blogCache")
  Cache blogByIdCacheProd(MeterRegistry registry, BlogFinder blogFinder) {
    Cache<String, BlogDTO> cache = Caffeine.newBuilder()
        .maximumSize(30000)
        .build();
    blogFinder.findAll()
        .forEach(blog -> cache.put(blog.getId(), BlogToDtoMapper.mapToBlogDto(blog)));
    registry.gauge("blogByIdCache", cache, Cache::estimatedSize);
    return cache;
  }
}
