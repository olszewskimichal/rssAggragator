package pl.michal.olszewski.rssaggregator.blog;

import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.scheduler.Schedulers;

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

  public Boolean updateBlog(Blog blog) {
    log.debug("START updateBlog dla blog {}", blog.getName());
    try {
      BlogDTO blogDTO = rssExtractorService.getBlog(
          new XmlReader(new URL(blog.getFeedURL())),
          blog.getFeedURL(),
          blog.getBlogURL(),    //TODO te 3 linie wyniesc do jakiejs klasy malej i dzieki temu skrócic linie
          blog.getLastUpdateDate() == null ? Instant.MIN : blog.getLastUpdateDate());
      blogService.updateBlog(blogDTO).subscribeOn(Schedulers.parallel()).block(); //TODO pozbyc sie blocka
      log.debug("STOP updateBlog dla blog {}", blog.getName());
      return true;
    } catch (IOException e) {
      log.error("wystapił bład przy aktualizacji bloga o id {}", blog.getId(), e);
      throw new RssException(blog.getFeedURL(), e);
    }
  }

}
