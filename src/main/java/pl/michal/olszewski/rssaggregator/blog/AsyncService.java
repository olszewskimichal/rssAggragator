package pl.michal.olszewski.rssaggregator.blog;

import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
class AsyncService {

  private final BlogService blogService;
  private final RssExtractorService rssExtractorService;

  public AsyncService(BlogService blogService) {
    this.blogService = blogService;
    this.rssExtractorService = new RssExtractorService();
  }

  @Async("threadPoolTaskExecutor")
  public Future<Void> updateBlog(Blog v) {
    log.debug("Przetwarzam blog {}", v.getName());
    try {
      BlogDTO blogDTO = rssExtractorService.getBlog(new XmlReader(new URL(v.getFeedURL())), v.getFeedURL(), v.getBlogURL(), v.getLastUpdateDate() == null ? Instant.MIN : v.getLastUpdateDate());
      blogService.updateBlog(blogDTO).block();
      return new AsyncResult<>(null);
    } catch (IOException e) {
      log.error("wystapił bład przy aktualizacji bloga o id {}", v.getId(), e);
      throw new RssException(v.getFeedURL(), e);
    }
  }

}
