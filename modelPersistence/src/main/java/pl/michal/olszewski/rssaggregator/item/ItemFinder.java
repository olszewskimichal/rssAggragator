package pl.michal.olszewski.rssaggregator.item;

import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class ItemFinder {
  private static final Logger log = LoggerFactory.getLogger(ItemFinder.class);

  private final ItemRepository itemRepository;
  private final ItemRepositorySync itemRepositorySync;

  ItemFinder(ItemRepository itemRepository, ItemRepositorySync itemRepositorySync) {
    this.itemRepository = itemRepository;
    this.itemRepositorySync = itemRepositorySync;
  }

  Mono<Item> findItemById(String id) {
    return itemRepository.findById(id);
  }

  Flux<Item> findAllOrderByPublishedDate(Integer limit, Integer page) {
    return itemRepository.findAllOrderByPublishedDate(limit, page);
  }

  Stream<Item> findAllOrderByPublishedDateBlocking(Integer limit, Integer page) {
    return itemRepositorySync.findAllOrderByPublishedDate(limit, page);
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
