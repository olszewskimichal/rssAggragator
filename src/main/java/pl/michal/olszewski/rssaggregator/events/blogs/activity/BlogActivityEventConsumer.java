package pl.michal.olszewski.rssaggregator.events.blogs.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class BlogActivityEventConsumer {

  private final BlogActivityUpdater activityUpdater;
  private final ChangeActivityBlogEventRepository activityBlogEventRepository;

  BlogActivityEventConsumer(BlogActivityUpdater activityUpdater, ChangeActivityBlogEventRepository activityBlogEventRepository) {
    this.activityUpdater = activityUpdater;
    this.activityBlogEventRepository = activityBlogEventRepository;
  }

  @JmsListener(destination = "activateBlogEvent")
  public void receiveActivateMessage(ActivateBlog event) {
    log.info("Received <{}>", event);
    activityBlogEventRepository.save(event)
        .flatMap(activateBlog -> activityUpdater.activateBlog(event.getBlogId()))
        .subscribe(blog -> log.debug("Blog {} activated", blog.getId()));
  }

  @JmsListener(destination = "deactivateBlogEvent")
  public void receiveDeactivateMessage(DeactivateBlog event) {
    log.info("Received <{}>", event);
    activityBlogEventRepository.save(event)
        .flatMap(deactivateBlog -> activityUpdater.deactivateBlog(event.getBlogId()))
        .subscribe(blog -> log.debug("Blog {} deactivated", blog.getId()));
  }
}
