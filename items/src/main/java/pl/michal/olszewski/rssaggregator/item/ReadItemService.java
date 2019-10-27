package pl.michal.olszewski.rssaggregator.item;

import org.springframework.stereotype.Component;

@Component
class ReadItemService {

  private final ItemRepository itemRepository;
  private final ItemUpdater itemUpdater;

  ReadItemService(ItemRepository itemRepository, ItemUpdater itemUpdater) {
    this.itemRepository = itemRepository;
    this.itemUpdater = itemUpdater;
  }

  void processRequest(ReadItemDTO readItemDTO) {
    Item item = itemRepository.findById(readItemDTO.getItemId())
        .orElseThrow(() -> new ItemNotFoundException(readItemDTO.getItemId()));
    if (readItemDTO.isRead()) {
      markItemAsRead(item);
    } else {
      markItemAsUnread(item);
    }
  }

  private void markItemAsRead(Item item) {
    item.markAsRead();
    itemUpdater.updateItem(item);
  }

  private void markItemAsUnread(Item item) {
    item.markAsUnread();
    itemUpdater.updateItem(item);
  }
}
