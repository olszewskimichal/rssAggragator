package pl.michal.olszewski.rssaggregator.item;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class ItemUpdater {

  private final ItemRepository itemRepository;

  ItemUpdater(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  Mono<Item> updateItem(Item item) {
    return itemRepository.save(item);
  }
}
