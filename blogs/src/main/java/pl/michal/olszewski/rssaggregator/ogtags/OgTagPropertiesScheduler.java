package pl.michal.olszewski.rssaggregator.ogtags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.BlogSyncRepository;

@Service
class OgTagPropertiesScheduler {

  private static final Logger log = LoggerFactory.getLogger(OgTagPropertiesScheduler.class);
  private final BlogSyncRepository blogSyncRepository;
  private final OgTagInfoUpdater ogTagInfoUpdater;

  OgTagPropertiesScheduler(BlogSyncRepository blogSyncRepository, OgTagInfoUpdater ogTagInfoUpdater) {
    this.blogSyncRepository = blogSyncRepository;
    this.ogTagInfoUpdater = ogTagInfoUpdater;
  }

  @Scheduled(cron = "0 0 7 * * ?")
  void updateBlogPropertiesFromOgTagsInfo() {
    log.info("updateBlogPropertiesFromOgTagsInfo STARTED");
    blogSyncRepository.findAll()
        .forEach(ogTagInfoUpdater::updateItemByOgTagInfo);
    log.info("updateBlogPropertiesFromOgTagsInfo FINISHED");
  }
}
