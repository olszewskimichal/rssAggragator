package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class BlogActivityUpdater {

  private final BlogFinder blogReactiveRepository;
  private final BlogWorker blogUpdater;

  BlogActivityUpdater(BlogFinder blogReactiveRepository, BlogWorker blogUpdater) {
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
