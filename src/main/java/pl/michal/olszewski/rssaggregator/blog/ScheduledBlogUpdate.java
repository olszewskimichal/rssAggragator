package pl.michal.olszewski.rssaggregator.blog;

import io.micrometer.core.annotation.Timed;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.config.RegistryTimed;

@Service
@Profile("prod")
@Slf4j
class ScheduledBlogUpdate {

  private final UpdateBlogService updateBlogService;

  ScheduledBlogUpdate(UpdateBlogService updateBlogService) {
    this.updateBlogService = updateBlogService;
  }

  @Scheduled(fixedDelayString = "${refresh.blog.milis}")
  @Timed(longTask = true, value = "scheduledUpdate")
  @RegistryTimed
  void runScheduledUpdate() {
    String correlationId = UUID.randomUUID().toString();
    log.debug("Rozpoczynam aktualizacje correlationId {}", correlationId);
    updateBlogService.updateAllActiveBlogsByRss(correlationId)
        .block();
  }

}
