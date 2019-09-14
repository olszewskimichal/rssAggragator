package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class NewItemInBlogEventConsumer {

  private final NewItemInBlogEventRepository newItemInBlogEventRepository;
  private final ItemRepository itemRepository;

  public NewItemInBlogEventConsumer(NewItemInBlogEventRepository newItemInBlogEventRepository, ItemRepository itemRepository) {
    this.newItemInBlogEventRepository = newItemInBlogEventRepository;
    this.itemRepository = itemRepository;
  }

  @JmsListener(destination = "newItems")
  public void receiveMessage(NewItemInBlogEvent event) {
    log.info("Received <{}>", event);
    newItemInBlogEventRepository.save(event);
    itemRepository.save(new Item(event.getItemDTO())).block();
    log.info("Event <{}>", event);
  }

}
