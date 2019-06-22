package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
class ReadItemService {

  private final ItemRepository itemRepository;

  ReadItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  private Mono<Void> markItemAsRead(Item item) {
    return Mono.fromCallable(item::markAsRead)
        .flatMap(itemRepository::save)
        .then();
  }

  private Mono<Void> markItemAsUnread(Item item) {
    return Mono.fromCallable(item::markAsUnread)
        .flatMap(itemRepository::save)
        .then();
  }

  Mono<Void> processRequest(ReadItemDTO readItemDTO, String correlationId) {
    return itemRepository.findById(readItemDTO.getItemId())
        .switchIfEmpty(Mono.error(new ItemNotFoundException(readItemDTO.getItemId(), correlationId)))
        .flatMap(item -> {
          if (readItemDTO.isRead()) {
            return markItemAsRead(item);
          }
          return markItemAsUnread(item);
        });
  }
}
