package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BlogFinder {

  private final BlogReactiveRepository blogReactiveRepository;

  BlogFinder(BlogReactiveRepository blogReactiveRepository) {
    this.blogReactiveRepository = blogReactiveRepository;
  }

  public Mono<Blog> findByFeedURL(String url) {
    return blogReactiveRepository.findByFeedURL(url);
  }

  public Mono<Blog> findByName(String name) {
    return blogReactiveRepository.findByName(name);
  }

  public Mono<Blog> findById(String id) {
    return blogReactiveRepository.findById(id);
  }

  public Flux<Blog> findAll() {
    return blogReactiveRepository.findAll();
  }

  Flux<BlogAggregationDTO> getBlogsWithCount() {
    return blogReactiveRepository.getBlogsWithCount();
  }

  public Mono<BlogAggregationDTO> getBlogWithCount(String id) {
    return blogReactiveRepository.getBlogWithCount(id);
  }
}
