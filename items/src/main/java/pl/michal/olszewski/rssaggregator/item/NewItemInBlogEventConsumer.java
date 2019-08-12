package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class NewItemInBlogEventConsumer {

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
