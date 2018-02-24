package pl.michal.olszewski.rssaggregator.service;

import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.exception.RssException;

@Service
@Transactional
@Slf4j
public class AsyncService {

  private final BlogService blogService;
  private final RssExtractorService rssExtractorService;

  public AsyncService(BlogService blogService) {
    this.blogService = blogService;
    this.rssExtractorService = new RssExtractorService();
  }

  @Async("threadPoolTaskExecutor")
  public Future<Void> updateBlog(Blog v) {
    log.debug("Przetwarzam blog {}", v.getName());
    CompletableFuture.supplyAsync(() -> {
      try {
        log.debug("updateBlog {} {}", v.getBlogURL(), Thread.currentThread().getName());
        return rssExtractorService.getBlog(new XmlReader(new URL(v.getFeedURL())), v.getFeedURL(), v.getBlogURL(), v.getLastUpdateDate() == null ? Instant.MIN : v.getLastUpdateDate());
      } catch (IOException e) {
        log.error("wystapił bład przy aktualizacji bloga o id {} o tresci {}", v.getId(), e);
        throw new RssException(v.getFeedURL());
      }
    }).thenCompose(blogDTO -> CompletableFuture.supplyAsync(() -> blogService.updateBlog(blogDTO)));
    return new AsyncResult<>(null);
  }

}
