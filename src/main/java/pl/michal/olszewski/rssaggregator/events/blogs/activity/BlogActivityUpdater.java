package pl.michal.olszewski.rssaggregator.events.blogs.activity;

import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogReactiveRepository;
import reactor.core.publisher.Mono;

@Service
public class BlogActivityUpdater {

  private final BlogReactiveRepository blogReactiveRepository;

  public BlogActivityUpdater(BlogReactiveRepository blogReactiveRepository) {
    this.blogReactiveRepository = blogReactiveRepository;
  }

  Mono<Blog> activateBlog(String id) {
    return blogReactiveRepository.findById(id)
        .flatMap(v -> {
          v.activate();
          return blogReactiveRepository.save(v);
        });
  }

  Mono<Blog> deactivateBlog(String id) {
    return blogReactiveRepository.findById(id)
        .flatMap(v -> {
          v.deactivate();
          return blogReactiveRepository.save(v);
        });
  }
}
