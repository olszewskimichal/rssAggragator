package pl.michal.olszewski.rssaggregator.blog;

import io.micrometer.core.annotation.Timed;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.config.RegistryTimed;
import reactor.core.publisher.Flux;

@Repository
class ReactiveAggregationBlogRepositoryImpl implements ReactiveAggregationBlogRepository {

  private static final String ITEMS = "items";
  private static final String NAME = "name";
  private static final String BLOG_URL = "blogURL";
  private static final String DESCRIPTION = "description";
  private static final String ID = "id";
  private static final String FEED_URL = "feedURL";
  private static final String PUBLISHED_DATE = "publishedDate";
  private static final String BLOG_ID = "blogId";
  private static final String LINK = "link";
  private static final String SIZE = "size";
  private static final String BLOG_ITEMS_COUNT = "blogItemsCount";
  private final ReactiveMongoTemplate reactiveMongoTemplate;

  public ReactiveAggregationBlogRepositoryImpl(ReactiveMongoTemplate reactiveMongoTemplate) {
    this.reactiveMongoTemplate = reactiveMongoTemplate;
  }

  @Override
  @RegistryTimed
  @Timed
  public Flux<BlogAggregationDTO> getBlogsWithCount() {
    return reactiveMongoTemplate.aggregate(Aggregation.newAggregation(
        Aggregation.unwind(ITEMS, true),
        Aggregation.group(ID, NAME, DESCRIPTION, FEED_URL, PUBLISHED_DATE, BLOG_URL)
            .addToSet(ITEMS).as(ITEMS),
        Aggregation.project()
            .and(ITEMS).project(SIZE).as(BLOG_ITEMS_COUNT)
            .and("_id." + ID).as(BLOG_ID)
            .and("_id." + NAME).as(NAME)
            .and("_id." + DESCRIPTION).as(DESCRIPTION)
            .and("_id." + FEED_URL).as(FEED_URL)
            .and("_id." + PUBLISHED_DATE).as(PUBLISHED_DATE)
            .and("_id." + BLOG_URL).as(LINK),
        Aggregation.sort(Direction.DESC, BLOG_ITEMS_COUNT)
    ), Blog.class, BlogAggregationDTO.class);
  }
}
