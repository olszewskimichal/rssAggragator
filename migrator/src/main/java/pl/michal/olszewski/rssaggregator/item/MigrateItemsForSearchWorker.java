package pl.michal.olszewski.rssaggregator.item;

import static pl.michal.olszewski.rssaggregator.item.LinkExtractor.getFinalURL;

import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEventBuilder;

@Service
class MigrateItemsForSearchWorker {

  private final ItemRepository itemRepository;
  private final MigrateItemForSearchEventProducer newItemForSearchEventProducer;

  MigrateItemsForSearchWorker(ItemRepository itemRepository, MigrateItemForSearchEventProducer newItemForSearchEventProducer) {
    this.itemRepository = itemRepository;
    this.newItemForSearchEventProducer = newItemForSearchEventProducer;
  }

  void migrateItemsForSearch() {
    itemRepository.findAllOrderByPublishedDate(Integer.MAX_VALUE, 0)
        .forEach(item -> newItemForSearchEventProducer.writeEventToQueue(new NewItemForSearchEventBuilder()
            .itemDescription(item.getDescription())
            .itemTitle(item.getTitle())
            .linkUrl(getFinalURL(item.getLink()))
            .build()));
  }
}
