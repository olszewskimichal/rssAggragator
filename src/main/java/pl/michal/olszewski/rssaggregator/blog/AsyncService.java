package pl.michal.olszewski.rssaggregator.blog;

import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
@Slf4j
class AsyncService {

  private static final Scheduler SCHEDULER = Schedulers.fromExecutor(Executors.newFixedThreadPool(100));
  private final BlogService blogService;
  private final RssExtractorService rssExtractorService;

  public AsyncService(BlogService blogService) {
    this.blogService = blogService;
    this.rssExtractorService = new RssExtractorService();
  }

  Boolean updateBlog(Blog blog) {
    log.debug("START updateBlog dla blog {}", blog.getName());
    try {
      var blogDTO = rssExtractorService.getBlog(new XmlReader(new URL(blog.getFeedURL())), blog.getRssInfo());
      blogService.updateBlog(blog, blogDTO)
          .subscribeOn(SCHEDULER)
          .doOnSuccess(v -> log.debug("STOP updateBlog dla blog {}", v.getName()))
          .block();
      return true;
    } catch (IOException e) {
      log.error("wystapił bład przy aktualizacji bloga o id {}", blog.getId(), e);
      throw new RssException(blog.getFeedURL(), e);
    }
  }

}
