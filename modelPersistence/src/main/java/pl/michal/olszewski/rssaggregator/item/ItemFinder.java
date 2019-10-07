package pl.michal.olszewski.rssaggregator.item;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.BlogNotFoundException;
import pl.michal.olszewski.rssaggregator.util.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
class ItemFinder {

  private final ItemRepository itemRepository;
  private final ItemRepositorySync itemRepositorySync;
  private final MongoTemplate mongoTemplate;

  ItemFinder(ItemRepository itemRepository, ItemRepositorySync itemRepositorySync, MongoTemplate mongoTemplate) {
    this.itemRepository = itemRepository;
    this.itemRepositorySync = itemRepositorySync;
    this.mongoTemplate = mongoTemplate;
  }

  Mono<Long> countAllItems() {
    return itemRepository.count().cache();
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

  Mono<PageBlogItemDTO> getBlogItemsForBlog(String blogId, Integer limit, Integer page) {
    Page pageable = new Page(limit, page);
    log.debug("getBlogItemsForBlog {}", blogId);
    boolean exists = mongoTemplate.exists(new Query().addCriteria(Criteria.where("_id").is(blogId)), "blog");
    if (!exists) {
      throw new BlogNotFoundException(blogId);
    }
    return itemRepository.findAllByBlogId(blogId)
        .collectList()
        .map(result -> new PageBlogItemDTO(
            result.stream().skip(pageable.getLimit() * pageable.getPageForSearch()).limit(pageable.getLimit()).map(ItemToDtoMapper::mapToBlogItemDTO).collect(Collectors.toList()),
            result.size())
        );
  }

  List<Item> findItemsFromDateOrderByCreatedAt(Instant from) {
    Query query = new Query();
    if (from != null) {
      query.addCriteria(Criteria.where("createdAt").gte(from));
    }
    query.with(Sort.by(Direction.DESC, "createdAt"));
    return mongoTemplate.find(query, Item.class);
  }
}
