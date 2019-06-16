package pl.michal.olszewski.rssaggregator.blog.failure;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class BlogUpdateFailedEventAggregator {

  private static final String ERROR_MSG = "errorMsg";
  private static final String BLOG_ID = "blogId";
  private static final String TOTAL = "total";
  private static final String OCCURRED_AT = "occurredAt";
  private final ReactiveMongoTemplate mongoTemplate;

  public BlogUpdateFailedEventAggregator(ReactiveMongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  //{ "$group" : { "_id" : { "blogId" : "$blogId" , "errorMsg" : "$errorMsg"} , "total" : { "$sum" : 1}}} , { "$match" : { "total" : { "$gt" : 1}}} , { "$project" : { "total" : 1 , "errorMsg" : "$_id.errorMsg" , "blogId" : "$_id.blogId"}}
  public Flux<UpdateBlogFailureCount> aggregateAllFailureOfBlogs() {
    return mongoTemplate.aggregate(Aggregation.newAggregation(
        groupByBlogIdAndErrorMsg(),
        whereTotalGreaterThanOne(),
        selectTotalAndBlogId(),
        orderByTotal()
    ), BlogUpdateFailedEvent.class, UpdateBlogFailureCount.class);
  }

  //{ "$match" : { "occurredAt" : { "$gte" : { "$date" : "2019-05-31T10:04:52.687Z"} , "$lte" : { "$date" : "2019-06-01T10:04:52.687Z"}}}} , { "$group" : { "_id" : { "blogId" : "$blogId" , "errorMsg" : "$errorMsg"} , "total" : { "$sum" : 1}}} , { "$match" : { "total" : { "$gt" : 1}}} , { "$project" : { "total" : 1 , "errorMsg" : "$_id.errorMsg" , "blogId" : "$_id.blogId"}}
  public Flux<UpdateBlogFailureCount> aggregateAllFailureOfBlogsFromPrevious24h() {
    return mongoTemplate.aggregate(Aggregation.newAggregation(
        whereOccurredAtFromLast24hours(),
        groupByBlogIdAndErrorMsg(),
        whereTotalGreaterThanOne(),
        selectTotalAndBlogId(),
        orderByTotal()
    ), BlogUpdateFailedEvent.class, UpdateBlogFailureCount.class);
  }

  private MatchOperation whereTotalGreaterThanOne() {
    return Aggregation.match(Criteria.where(TOTAL).gt(1L));
  }

  private SortOperation orderByTotal() {
    return sort(Direction.DESC, "total");
  }

  private ProjectionOperation selectTotalAndBlogId() {
    return Aggregation.project(TOTAL)
        .and("_id.errorMsg").as(ERROR_MSG)
        .and("_id.blogId").as(BLOG_ID);
  }

  private GroupOperation groupByBlogIdAndErrorMsg() {
    return Aggregation.group(BLOG_ID, ERROR_MSG)
        .count().as(TOTAL);
  }

  private AggregationOperation whereOccurredAtFromLast24hours() {
    return Aggregation.match(
        Criteria.where(OCCURRED_AT)
            .gte(Instant.now().minus(1, ChronoUnit.DAYS))
            .lte(Instant.now()));

  }
}