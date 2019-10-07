package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
class ReactiveAggregationBlogRepositoryImpl implements ReactiveAggregationBlogRepository {

  private static final String ITEMS = "items";
  private static final String ID = "id";
  private static final String BLOG_ID = "blogId";
  private static final String SIZE = "size";
  private static final String BLOG_ITEMS_COUNT = "blogItemsCount";
  private final ReactiveMongoTemplate reactiveMongoTemplate;

  public ReactiveAggregationBlogRepositoryImpl(ReactiveMongoTemplate reactiveMongoTemplate) {
    this.reactiveMongoTemplate = reactiveMongoTemplate;
  }

  @Override
  public Mono<BlogAggregationDTO> getBlogWithCount(String id) {
    LookupOperation lookupOperation = LookupOperation.newLookup()
        .from("item")
        .localField(BLOG_ID)
        .foreignField(ID).as(ITEMS);

    return reactiveMongoTemplate.aggregate(Aggregation.newAggregation(
        Aggregation.match(Criteria.where(ID).is(id)),
        lookupOperation,
        Aggregation.project()
            .and(ITEMS).project(SIZE).as(BLOG_ITEMS_COUNT)
            .and(ID).as(BLOG_ID),
        Aggregation.sort(Direction.DESC, BLOG_ITEMS_COUNT)
    ), Blog.class, BlogAggregationDTO.class).single();
  }
}
