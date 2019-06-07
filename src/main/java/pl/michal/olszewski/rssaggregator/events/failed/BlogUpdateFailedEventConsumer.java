package pl.michal.olszewski.rssaggregator.events.failed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class BlogUpdateFailedEventConsumer {

  private final BlogUpdateFailedEventRepository blogUpdateFailedEventRepository;

  public BlogUpdateFailedEventConsumer(BlogUpdateFailedEventRepository blogUpdateFailedEventRepository) {
    this.blogUpdateFailedEventRepository = blogUpdateFailedEventRepository;
  }

  @JmsListener(destination = "exceptionEvents")
  public void receiveMessage(BlogUpdateFailedEvent event) {
    log.info("Received <{}>", event);
    blogUpdateFailedEventRepository.save(event);
    log.info("Event <{}>", event);
  }

}
