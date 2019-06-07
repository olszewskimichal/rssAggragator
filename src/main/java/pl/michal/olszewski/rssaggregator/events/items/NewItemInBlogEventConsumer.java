package pl.michal.olszewski.rssaggregator.events.items;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class NewItemInBlogEventConsumer {

  private final NewItemInBlogEventRepository newItemInBlogEventRepository;

  public NewItemInBlogEventConsumer(NewItemInBlogEventRepository newItemInBlogEventRepository) {
    this.newItemInBlogEventRepository = newItemInBlogEventRepository;
  }

  @JmsListener(destination = "newItems")
  public void receiveMessage(NewItemInBlogEvent event) {
    log.info("Received <{}>", event);
    newItemInBlogEventRepository.save(event);
    log.info("Event <{}>", event);
  }

}
