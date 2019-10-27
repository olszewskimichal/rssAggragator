package pl.michal.olszewski.rssaggregator.item;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

@Service
class ItemFinder {

  private static final Logger log = LoggerFactory.getLogger(ItemFinder.class);

  private final ItemRepository itemRepository;
  private final MongoTemplate mongoTemplate;

  ItemFinder(ItemRepository itemRepository, MongoTemplate mongoTemplate) {
    this.itemRepository = itemRepository;
    this.mongoTemplate = mongoTemplate;
  }

  Long countAllItems() {
    return itemRepository.count();
  }

  Optional<Item> findItemById(String id) {
    return itemRepository.findById(id);
  }

  List<Item> findAllOrderByPublishedDate(Integer limit, Integer page) {
    return itemRepository.findAllOrderByPublishedDate(limit, page);
  }

  List<Item> findAllOrderByCreatedAt(Integer limit, Integer page) {
    return itemRepository.findAllOrderByCreatedAt(limit, page);
  }

  PageBlogItemDTO getBlogItemsForBlog(String blogId, Integer limit, Integer page) {
    Page pageable = new Page(limit, page);
    log.debug("getBlogItemsForBlog {}", blogId);
    boolean exists = mongoTemplate.exists(new Query().addCriteria(Criteria.where("_id").is(blogId)), "blog");
    if (!exists) {
      throw new BlogNotFoundException(blogId);
    }
    List<Item> allByBlogId = itemRepository.findAllByBlogId(blogId);
    List<BlogItemDTO> blogItemDTOS = allByBlogId.stream()
        .skip(pageable.getLimit() * pageable.getPageForSearch())
        .limit(pageable.getLimit())
        .map(ItemToDtoMapper::mapToBlogItemDTO)
        .collect(Collectors.toList());
    return new PageBlogItemDTO(
        blogItemDTOS,
        allByBlogId.size()
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
