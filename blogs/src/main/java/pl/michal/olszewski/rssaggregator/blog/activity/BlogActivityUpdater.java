package pl.michal.olszewski.rssaggregator.blog.activity;

import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogFinder;
import pl.michal.olszewski.rssaggregator.blog.BlogNotFoundException;
import pl.michal.olszewski.rssaggregator.blog.BlogUpdater;
import reactor.core.publisher.Mono;

@Service
public class BlogActivityUpdater {

  private final BlogFinder blogReactiveRepository;
  private final BlogUpdater blogUpdater;

  public BlogActivityUpdater(BlogFinder blogReactiveRepository, BlogUpdater blogUpdater) {
    this.blogReactiveRepository = blogReactiveRepository;
    this.blogUpdater = blogUpdater;
  }

  Mono<Blog> activateBlog(String id) {
    return blogReactiveRepository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(blogUpdater::activateBlog);
  }

  Mono<Blog> deactivateBlog(String id) {
    return blogReactiveRepository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(blogUpdater::deactivateBlog);
  }
}
