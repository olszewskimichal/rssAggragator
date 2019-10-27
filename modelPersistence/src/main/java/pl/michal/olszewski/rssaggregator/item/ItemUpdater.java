package pl.michal.olszewski.rssaggregator.item;

import org.springframework.stereotype.Service;

@Service
class ItemUpdater {

  private final ItemRepository itemRepository;

  ItemUpdater(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  Item updateItem(Item item) {
    return itemRepository.save(item);
  }
}
