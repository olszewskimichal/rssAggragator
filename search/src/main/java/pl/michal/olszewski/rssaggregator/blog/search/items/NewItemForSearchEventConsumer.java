package pl.michal.olszewski.rssaggregator.blog.search.items;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;

@Component
@Slf4j
public class NewItemForSearchEventConsumer {

  private final ItemForSearchRepository itemForSearchRepository;

  public NewItemForSearchEventConsumer(ItemForSearchRepository itemForSearchRepository) {
    this.itemForSearchRepository = itemForSearchRepository;
  }

  @JmsListener(destination = "searchItems")
  public void receiveMessage(NewItemForSearchEvent event) {
    log.info("Received <{}>", event);
    itemForSearchRepository.save(ItemForSearch.builder()
        .link(event.getLinkUrl())
        .title(event.getItemTitle())
        .description(event.getItemDescription())
        .build());
    log.info("Event <{}>", event);
  }

}
