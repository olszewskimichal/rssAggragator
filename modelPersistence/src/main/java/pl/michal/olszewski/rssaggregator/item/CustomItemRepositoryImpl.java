package pl.michal.olszewski.rssaggregator.item;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.blog.BlogNotFoundException;
import pl.michal.olszewski.rssaggregator.util.Page;

@Repository
class CustomItemRepositoryImpl implements CustomItemRepository {

  private final MongoTemplate mongoTemplate;

  public CustomItemRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public List<Item> findItemsFromDateOrderByCreatedAt(Instant from) {
    Query query = new Query();
    if (from != null) {
      query.addCriteria(Criteria.where("createdAt").gte(from));
    }
    query.with(Sort.by(Direction.DESC, "createdAt"));
    return mongoTemplate.find(query, Item.class);
  }

  @Override
  public PageBlogItemDTO getBlogItemsForBlog(String blogId, Integer limit, Integer page) {
    boolean exists = mongoTemplate.exists(new Query().addCriteria(Criteria.where("_id").is(blogId)), "blog");
    if (!exists) {
      throw new BlogNotFoundException(blogId);
    }
    Page pageable = new Page(limit, page);
    List<Item> allByBlogId = mongoTemplate.find(new Query(Criteria.where("blogId").is(blogId)), Item.class);
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
}
