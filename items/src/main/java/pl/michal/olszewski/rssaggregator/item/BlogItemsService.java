package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
class BlogItemsService {

  private final ItemRepository itemRepository;

  BlogItemsService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  Flux<BlogItemDTO> getBlogItemsForBlog(String blogId) {
    log.debug("getBlogItemsForBlog {}", blogId);
    return itemRepository.findAllByBlogId(blogId)
        .map(BlogItemDTO::new);
  }

}
