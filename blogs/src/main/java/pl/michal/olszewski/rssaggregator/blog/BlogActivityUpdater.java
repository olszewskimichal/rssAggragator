package pl.michal.olszewski.rssaggregator.blog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class BlogActivityUpdater {

  private static final Logger log = LoggerFactory.getLogger(BlogActivityUpdater.class);
  private final BlogFinder blogReactiveRepository;
  private final BlogWorker blogUpdater;

  private BlogActivityUpdater(BlogFinder blogReactiveRepository, BlogWorker blogUpdater) {
    this.blogReactiveRepository = blogReactiveRepository;
    this.blogUpdater = blogUpdater;
  }

  Mono<Blog> activateBlog(String id) {
    log.debug("Activate blog by id {}", id);
    return blogReactiveRepository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(blogUpdater::activateBlog);
  }

  Mono<Blog> deactivateBlog(String id) {
    log.debug("Deactivate blog by id {}", id);
    return blogReactiveRepository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(blogUpdater::deactivateBlog);
  }
}
