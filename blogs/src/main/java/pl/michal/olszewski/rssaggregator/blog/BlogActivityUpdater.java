package pl.michal.olszewski.rssaggregator.blog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class BlogActivityUpdater {

  private static final Logger log = LoggerFactory.getLogger(BlogActivityUpdater.class);
  private final BlogRepository blogRepository;
  private final BlogWorker blogUpdater;

  BlogActivityUpdater(BlogRepository blogRepository, BlogWorker blogUpdater) {
    this.blogRepository = blogRepository;
    this.blogUpdater = blogUpdater;
  }

  Blog activateBlog(String id) {
    log.debug("Activate blog by id {}", id);
    return blogRepository.findById(id)
        .map(blogUpdater::activateBlog)
        .orElseThrow(() -> new BlogNotFoundException(id));
  }

  Blog deactivateBlog(String id) {
    log.debug("Deactivate blog by id {}", id);
    return blogRepository.findById(id)
        .map(blogUpdater::deactivateBlog)
        .orElseThrow(() -> new BlogNotFoundException(id));
  }
}
