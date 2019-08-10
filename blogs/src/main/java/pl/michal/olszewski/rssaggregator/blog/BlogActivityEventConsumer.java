package pl.michal.olszewski.rssaggregator.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class BlogActivityEventConsumer {

  private final BlogActivityUpdater activityUpdater;

  BlogActivityEventConsumer(BlogActivityUpdater activityUpdater) {
    this.activityUpdater = activityUpdater;
  }

  @JmsListener(destination = "activateBlogEvent")
  public void receiveActivateMessage(ActivateBlog event) {
    log.info("Received <{}>", event);
    activityUpdater.activateBlog(event.getBlogId())
        .doOnSuccess(blog -> log.debug("Blog {} activated", blog.getId()))
        .block();
  }

  @JmsListener(destination = "deactivateBlogEvent")
  public void receiveDeactivateMessage(DeactivateBlog event) {
    log.info("Received <{}>", event);
    activityUpdater.deactivateBlog(event.getBlogId())
        .doOnSuccess(blog -> log.debug("Blog {} deactivated", blog.getId()))
        .block();
  }
}
