package pl.michal.olszewski.rssaggregator.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
class NewItemInBlogEventConsumer {

  private static final Logger log = LoggerFactory.getLogger(NewItemInBlogEventConsumer.class);
  private final ItemSaver itemSaver;

  NewItemInBlogEventConsumer(ItemSaver itemSaver) {
    this.itemSaver = itemSaver;
  }

  @JmsListener(destination = "newItems")
  public void receiveMessage(NewItemInBlogEvent event) {
    log.info("Received <{}>", event);
    itemSaver.saveNewItem(event.getItemDTO());
    log.info("Event <{}>", event);
  }

}
