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

  public Boolean updateBlog(Blog v) {
    log.debug("START updateBlog dla blog {}", v.getName());
    try {
      BlogDTO blogDTO = rssExtractorService.getBlog(new XmlReader(new URL(v.getFeedURL())), v.getFeedURL(), v.getBlogURL(), v.getLastUpdateDate() == null ? Instant.MIN : v.getLastUpdateDate()); //TODO skrocic linie
      blogService.updateBlog(blogDTO).subscribeOn(Schedulers.parallel()).block(); //TODO pozbyc sie blocka
      log.debug("STOP updateBlog dla blog {}", v.getName());
      return true;
    } catch (IOException e) {
      log.error("wystapił bład przy aktualizacji bloga o id {}", v.getId(), e);
      throw new RssException(v.getFeedURL(), e);
    }
  }

}
