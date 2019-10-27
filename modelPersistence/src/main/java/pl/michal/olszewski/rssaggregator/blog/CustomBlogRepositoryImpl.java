package pl.michal.olszewski.rssaggregator.blog;

import java.util.Optional;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
class CustomBlogRepositoryImpl implements CustomBlogRepository {

  private static final String ITEMS = "items";
  private static final String ID = "id";
  private static final String BLOG_ID = "blogId";
  private static final String SIZE = "size";
  private static final String BLOG_ITEMS_COUNT = "blogItemsCount";
  private final MongoTemplate mongoTemplate;

  public CustomBlogRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Optional<BlogAggregationDTO> getBlogWithCount(String id) {
    LookupOperation lookupOperation = LookupOperation.newLookup()
        .from("item")
        .localField(BLOG_ID)
        .foreignField(ID).as(ITEMS);

    return Optional.ofNullable(mongoTemplate.aggregate(Aggregation.newAggregation(
        Aggregation.match(Criteria.where(ID).is(id)),
        lookupOperation,
        Aggregation.project()
            .and(ITEMS).project(SIZE).as(BLOG_ITEMS_COUNT)
            .and(ID).as(BLOG_ID),
        Aggregation.sort(Direction.DESC, BLOG_ITEMS_COUNT)
    ), Blog.class, BlogAggregationDTO.class).getUniqueMappedResult());
  }
}
