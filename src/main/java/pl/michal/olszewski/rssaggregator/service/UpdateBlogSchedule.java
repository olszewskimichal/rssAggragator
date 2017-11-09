package pl.michal.olszewski.rssaggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateBlogSchedule {

  private final BlogService blogService;
  private final AsyncService asyncService;

  @Value("${enableJob}")
  private boolean enableJob;


  public UpdateBlogSchedule(BlogService blogService, AsyncService asyncService) {
    this.blogService = blogService;
    this.asyncService = asyncService;
  }

  @Scheduled(fixedDelay = 5 * 60 * 1000)
  public void updatesBlogs() {
    if (enableJob) {
      log.debug("zaczynam aktualizacje blogów");
      blogService.getAllBlogs().forEach(asyncService::updateBlog);
      log.debug("Aktualizacja zakończona");
    }
  }

}
