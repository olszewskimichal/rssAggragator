package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
class ItemFinder {

  private final ItemRepository itemRepository;

  ItemFinder(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  Mono<Item> findItemById(String id) {
    return itemRepository.findById(id);
  }

  Flux<Item> findAllOrderByPublishedDate(Integer limit, Integer page) {
    return itemRepository.findAllOrderByPublishedDate(limit, page);
  }

  Flux<Item> findAllOrderByCreatedAt(Integer limit, Integer page) {
    return itemRepository.findAllOrderByCreatedAt(limit, page);
  }

  Flux<BlogItemDTO> getBlogItemsForBlog(String blogId) {
    log.debug("getBlogItemsForBlog {}", blogId);
    return itemRepository.findAllByBlogId(blogId)
        .map(ItemToDtoMapper::mapToBlogItemDTO);
  }
}
