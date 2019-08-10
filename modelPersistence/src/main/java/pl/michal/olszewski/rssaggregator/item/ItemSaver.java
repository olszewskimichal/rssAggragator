package pl.michal.olszewski.rssaggregator.item;

import org.springframework.stereotype.Service;

@Service
class ItemSaver {

  private final ItemRepository itemRepository;

  ItemSaver(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  void saveNewItem(ItemDTO itemDTO) {
    itemRepository.save(new Item(itemDTO)).block();
  }
}
