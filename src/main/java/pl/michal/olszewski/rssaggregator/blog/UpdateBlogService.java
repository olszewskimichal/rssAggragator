package pl.michal.olszewski.rssaggregator.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
class UpdateBlogService {

  private final BlogRepository repository;
  private final AsyncService asyncService;

  @Value("${refresh.blog.enable-job}")
  private boolean enableJob;


  public UpdateBlogService(BlogRepository repository, AsyncService asyncService) {
    this.repository = repository;
    this.asyncService = asyncService;
  }

  @Scheduled(fixedDelayString = "${refresh.blog.milis}")
  public void updatesBlogs() {
    if (enableJob) {
      log.debug("zaczynam aktualizacje blogów");
      repository.findAll()
          .parallelStream()
          .forEach(asyncService::updateBlog);
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
