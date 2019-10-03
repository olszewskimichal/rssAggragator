package pl.michal.olszewski.rssaggregator.blog;

import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BlogFinder {

  private final BlogReactiveRepository blogReactiveRepository;
  private final BlogSyncRepository blogSyncRepository;

  BlogFinder(BlogReactiveRepository blogReactiveRepository, BlogSyncRepository blogSyncRepository) {
    this.blogReactiveRepository = blogReactiveRepository;
    this.blogSyncRepository = blogSyncRepository;
  }

  public Flux<Blog> findAll() {
    return blogReactiveRepository.findAll();
  }

  public Mono<BlogAggregationDTO> getBlogWithCount(String id) {
    return blogReactiveRepository.getBlogWithCount(id);
  }

  List<Blog> findAllSync() {
    return blogSyncRepository.findAll();
  }

  Mono<Blog> findByFeedURL(String url) {
    return blogReactiveRepository.findByFeedURL(url);
  }

  Mono<Blog> findById(String id) {
    return blogReactiveRepository.findById(id);
  }
}
