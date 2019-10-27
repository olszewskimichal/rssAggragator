package pl.michal.olszewski.rssaggregator.ogtags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.BlogRepository;

@Service
class OgTagPropertiesScheduler {

  private static final Logger log = LoggerFactory.getLogger(OgTagPropertiesScheduler.class);
  private final BlogRepository blogRepository;
  private final OgTagInfoUpdater ogTagInfoUpdater;

  OgTagPropertiesScheduler(BlogRepository blogRepository, OgTagInfoUpdater ogTagInfoUpdater) {
    this.blogRepository = blogRepository;
    this.ogTagInfoUpdater = ogTagInfoUpdater;
  }

  @Scheduled(cron = "0 0 7 * * ?")
  void updateBlogPropertiesFromOgTagsInfo() {
    log.info("updateBlogPropertiesFromOgTagsInfo STARTED");
    blogRepository.findAll()
        .forEach(ogTagInfoUpdater::updateItemByOgTagInfo);
    log.info("updateBlogPropertiesFromOgTagsInfo FINISHED");
  }
}
