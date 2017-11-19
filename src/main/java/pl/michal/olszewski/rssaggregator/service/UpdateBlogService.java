package pl.michal.olszewski.rssaggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.exception.BlogNotFoundException;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

@Service
@Slf4j
@Transactional
public class UpdateBlogService {

  private final BlogRepository repository;
  private final AsyncService asyncService;

  @Value("${enableJob}")
  private boolean enableJob;


  public UpdateBlogService(BlogRepository repository, AsyncService asyncService) {
    this.repository = repository;
    this.asyncService = asyncService;
  }

  @Scheduled(fixedDelay = 5 * 60 * 1000)
  public void updatesBlogs() {
    if (enableJob) {
      log.debug("zaczynam aktualizacje blogów");
      repository.findStreamAll().forEach(asyncService::updateBlog);
      log.debug("Aktualizacja zakończona");
    }
  }

  public void updateBlogFromId(Long id) {
    log.debug("Odswiezam bloga o id {}", id);
    Blog blog = repository.findById(id)
        .orElseThrow(() -> new BlogNotFoundException(id));
    asyncService.updateBlog(blog);
  }

}
