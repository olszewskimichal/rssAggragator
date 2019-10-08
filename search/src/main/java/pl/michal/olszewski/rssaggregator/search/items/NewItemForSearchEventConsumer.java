package pl.michal.olszewski.rssaggregator.search.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;

@Component
class NewItemForSearchEventConsumer {
  private static final Logger log = LoggerFactory.getLogger(NewItemForSearchEventConsumer.class);
  private final ItemSearchService itemSearchService;

  private NewItemForSearchEventConsumer(ItemSearchService itemSearchService) {
    this.itemSearchService = itemSearchService;
  }

  @JmsListener(destination = "searchItems")
  public void receiveMessage(NewItemForSearchEvent event) {
    log.info("Received <{}>", event);
    itemSearchService.saveItemForSearch(new ItemForSearchBuilder()
        .link(event.getLinkUrl())
        .title(event.getItemTitle())
        .description(event.getItemDescription())
        .build());
    log.info("Event <{}>", event);
  }

}
