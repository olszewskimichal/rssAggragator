package pl.michal.olszewski.rssaggregator.blog.ogtags;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogSyncRepository;

@Service
class OgTagPropertiesScheduler {

  private static final Logger log = LoggerFactory.getLogger(OgTagPropertiesScheduler.class);
  private final BlogSyncRepository blogSyncRepository;
  private final OgTagBlogUpdater ogTagBlogUpdater;

  OgTagPropertiesScheduler(BlogSyncRepository blogSyncRepository, OgTagBlogUpdater ogTagBlogUpdater) {
    this.blogSyncRepository = blogSyncRepository;
    this.ogTagBlogUpdater = ogTagBlogUpdater;
  }

  @Scheduled(cron = "0 0 7 * * ?")
  void updateBlogPropertiesFromOgTagsInfo() {
    log.info("updateBlogPropertiesFromOgTagsInfo STARTED");
    List<Blog> collect = blogSyncRepository.findAll().stream()
        .map(ogTagBlogUpdater::updateBlogByOgTagInfo)
        .collect(Collectors.toList());
    log.info("updateBlogPropertiesFromOgTagsInfo FINISHED");
  }
}
