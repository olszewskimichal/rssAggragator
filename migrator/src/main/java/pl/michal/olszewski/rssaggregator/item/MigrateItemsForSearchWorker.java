package pl.michal.olszewski.rssaggregator.item;

import static pl.michal.olszewski.rssaggregator.item.LinkExtractor.getFinalURL;

import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEventBuilder;

@Service
class MigrateItemsForSearchWorker {

  private final ItemFinder itemFinder;
  private final MigrateItemForSearchEventProducer newItemForSearchEventProducer;

  MigrateItemsForSearchWorker(ItemFinder itemFinder, MigrateItemForSearchEventProducer newItemForSearchEventProducer) {
    this.itemFinder = itemFinder;
    this.newItemForSearchEventProducer = newItemForSearchEventProducer;
  }

  void migrateItemsForSearch() {
    itemFinder.findAllOrderByPublishedDate(Integer.MAX_VALUE, 0)
        .forEach(item -> newItemForSearchEventProducer.writeEventToQueue(new NewItemForSearchEventBuilder()
            .itemDescription(item.getDescription())
            .itemTitle(item.getTitle())
            .linkUrl(getFinalURL(item.getLink()))
            .build()));
  }
}
