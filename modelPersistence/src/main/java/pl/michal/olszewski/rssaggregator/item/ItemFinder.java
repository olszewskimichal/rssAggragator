package pl.michal.olszewski.rssaggregator.item;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
class ItemFinder {

  private static final Logger log = LoggerFactory.getLogger(ItemFinder.class);

  private final ItemRepository itemRepository;
  private final MongoTemplate mongoTemplate;

  ItemFinder(ItemRepository itemRepository, MongoTemplate mongoTemplate) {
    this.itemRepository = itemRepository;
    this.mongoTemplate = mongoTemplate;
  }

  Mono<Long> countAllItems() {
    return Mono.just(itemRepository.count());
  }

  Optional<Item> findItemById(String id) {
    return itemRepository.findById(id);
  }

  Flux<Item> findAllOrderByPublishedDate(Integer limit, Integer page) {
    return Flux.fromIterable(itemRepository.findAllOrderByPublishedDate(limit, page));
  }

  Stream<Item> findAllOrderByPublishedDateBlocking(Integer limit, Integer page) {
    return itemRepository.findAllOrderByPublishedDate(limit, page).stream();
  }

  Flux<Item> findAllOrderByCreatedAt(Integer limit, Integer page) {
    return Flux.fromIterable(itemRepository.findAllOrderByCreatedAt(limit, page));
  }

  Mono<PageBlogItemDTO> getBlogItemsForBlog(String blogId, Integer limit, Integer page) {
    Page pageable = new Page(limit, page);
    log.debug("getBlogItemsForBlog {}", blogId);
    boolean exists = mongoTemplate.exists(new Query().addCriteria(Criteria.where("_id").is(blogId)), "blog");
    if (!exists) {
      throw new BlogNotFoundException(blogId);
    }
    return Flux.fromIterable(itemRepository.findAllByBlogId(blogId))
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
