package pl.michal.olszewski.rssaggregator.blog.rss.update;

import com.google.common.base.Stopwatch;
import io.micrometer.core.annotation.Timed;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile({"prod", "development"})
class ScheduledBlogUpdate {

  private static final Logger log = LoggerFactory.getLogger(ScheduledBlogUpdate.class);
  private final UpdateBlogService updateBlogService;

  ScheduledBlogUpdate(UpdateBlogService updateBlogService) {
    this.updateBlogService = updateBlogService;
  }

  @Scheduled(fixedDelayString = "${refresh.blog.milis}")
  @Timed(longTask = true, value = "scheduledUpdate")
  void runScheduledUpdate() {
    log.debug("Rozpoczynam aktualizacje");
    Stopwatch stopwatch = Stopwatch.createStarted();
    updateBlogService.updateAllActiveBlogsByRss()
        .doOnComplete(() -> log.debug("Zakonczono w czasie {} milisekund", stopwatch.elapsed(TimeUnit.MILLISECONDS)))
        .then()
        .block();
  }

}
