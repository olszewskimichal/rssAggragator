package pl.michal.olszewski.rssaggregator.item;

import static java.lang.Integer.MAX_VALUE;
import static pl.michal.olszewski.rssaggregator.item.LinkExtractor.getFinalURL;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;

@Service
class MigrateItemsForSearchWorker {

  private final ItemFinder itemFinder;
  private final MigrateItemForSearchEventProducer newItemForSearchEventProducer;

  MigrateItemsForSearchWorker(ItemFinder itemFinder, MigrateItemForSearchEventProducer newItemForSearchEventProducer) {
    this.itemFinder = itemFinder;
    this.newItemForSearchEventProducer = newItemForSearchEventProducer;
  }

  void migrateItemsForSearch() {
    itemFinder.findAllOrderByPublishedDateBlocking(MAX_VALUE, 0)
        .forEach(item -> newItemForSearchEventProducer.writeEventToQueue(NewItemForSearchEvent.builder()
            .itemDescription(item.getDescription())
            .itemTitle(item.getTitle())
            .linkUrl(getFinalURL(item.getLink()))
            .occurredAt(Instant.now())
            .build()));
  }
}
