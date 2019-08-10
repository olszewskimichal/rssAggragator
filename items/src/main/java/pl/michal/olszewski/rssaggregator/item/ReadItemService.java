package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
class ReadItemService {

  private final ItemFinder itemFinder;
  private final ItemUpdater itemUpdater;

  ReadItemService(ItemFinder itemFinder, ItemUpdater itemUpdater) {
    this.itemFinder = itemFinder;
    this.itemUpdater = itemUpdater;
  }

  private Mono<Void> markItemAsRead(Item item) {
    return Mono.fromCallable(item::markAsRead)
        .flatMap(itemUpdater::updateItem)
        .then();
  }

  private Mono<Void> markItemAsUnread(Item item) {
    return Mono.fromCallable(item::markAsUnread)
        .flatMap(itemUpdater::updateItem)
        .then();
  }

  Mono<Void> processRequest(ReadItemDTO readItemDTO) {
    return itemFinder.findItemById(readItemDTO.getItemId())
        .switchIfEmpty(Mono.error(new ItemNotFoundException(readItemDTO.getItemId())))
        .flatMap(item -> {
          if (readItemDTO.isRead()) {
            return markItemAsRead(item);
          }
          return markItemAsUnread(item);
        });
  }
}
