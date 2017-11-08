package pl.michal.olszewski.rssaggregator.service;

import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.exception.RssException;

@Service
@Slf4j
public class UpdateBlogSchedule {

  private final BlogService blogService;
  private final RssExtractorService rssExtractorService;

  @Value("${enableJob}")
  private boolean enableJob;


  public UpdateBlogSchedule(BlogService blogService) {
    this.blogService = blogService;
    this.rssExtractorService = new RssExtractorService();
  }

  @Scheduled(fixedDelay = 10 * 60 * 1000)
  public void updatesBlogs() {
    if (enableJob) {
      log.debug("zaczynam aktualizacje blogów");
      blogService.getAllBlogs().forEach(this::updateBlog);
      log.debug("Aktualizacja zakończona");
    }
  }

  private void updateBlog(Blog v) {
    try {
      BlogDTO blogDTO = rssExtractorService.getBlog(new XmlReader(new URL(v.getFeedURL())), v.getFeedURL(), v.getBlogURL());
      blogService.updateBlog(blogDTO);
    } catch (IOException e) {
      log.error("aaa" + e.getMessage());
      log.error("wystapił bład przy aktualizacji bloga o id {} o tresci {}", v.getId(), e);
      throw new RssException(v.getFeedURL());
    }
  }
}
