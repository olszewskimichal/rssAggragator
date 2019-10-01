package pl.michal.olszewski.rssaggregator.blog;

import static reactor.core.publisher.Mono.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
class BlogActivityUpdater {

  private final BlogFinder blogReactiveRepository;
  private final BlogWorker blogUpdater;

  BlogActivityUpdater(BlogFinder blogReactiveRepository, BlogWorker blogUpdater) {
    this.blogReactiveRepository = blogReactiveRepository;
    this.blogUpdater = blogUpdater;
  }

  Mono<Blog> activateBlog(String id) {
    log.debug("Activate blog by id {}", id);
    return blogReactiveRepository.findById(id)
        .switchIfEmpty(error(new BlogNotFoundException(id)))
        .flatMap(blogUpdater::activateBlog);
  }

  Mono<Blog> deactivateBlog(String id) {
    log.debug("Deactivate blog by id {}", id);
    return blogReactiveRepository.findById(id)
        .switchIfEmpty(error(new BlogNotFoundException(id)))
        .flatMap(blogUpdater::deactivateBlog);
  }
}
