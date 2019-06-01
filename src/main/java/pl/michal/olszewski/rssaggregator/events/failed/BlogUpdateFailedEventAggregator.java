package pl.michal.olszewski.rssaggregator.events.failed;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class BlogUpdateFailedEventAggregator {

  private static final String ERROR_MSG = "errorMsg";
  private static final String BLOG_ID = "blogId";
  private static final String TOTAL = "total";
  private static final String OCCURRED_AT = "occurredAt";
  private final MongoTemplate mongoTemplate;

  public BlogUpdateFailedEventAggregator(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  //{ "$group" : { "_id" : { "blogId" : "$blogId" , "errorMsg" : "$errorMsg"} , "total" : { "$sum" : 1}}} , { "$match" : { "total" : { "$gt" : 1}}} , { "$project" : { "total" : 1 , "errorMsg" : "$_id.errorMsg" , "blogId" : "$_id.blogId"}}
  List<UpdateBlogFailureCount> aggregateAllFailureOfBlogs() {
    return mongoTemplate.aggregate(Aggregation.newAggregation(
        groupByBlogIdAndErrorMsg(),
        whereTotalGreaterThanOne(),
        selectTotalAndBlogId()
    ), BlogUpdateFailedEvent.class, UpdateBlogFailureCount.class).getMappedResults();
  }

  //{ "$match" : { "occurredAt" : { "$gte" : { "$date" : "2019-05-31T10:04:52.687Z"} , "$lte" : { "$date" : "2019-06-01T10:04:52.687Z"}}}} , { "$group" : { "_id" : { "blogId" : "$blogId" , "errorMsg" : "$errorMsg"} , "total" : { "$sum" : 1}}} , { "$match" : { "total" : { "$gt" : 1}}} , { "$project" : { "total" : 1 , "errorMsg" : "$_id.errorMsg" , "blogId" : "$_id.blogId"}}
  List<UpdateBlogFailureCount> aggregateAllFailureOfBlogsFromPrevious24h() {
    return mongoTemplate.aggregate(Aggregation.newAggregation(
        whereOccurredAtFromLast24hours(),
        groupByBlogIdAndErrorMsg(),
        whereTotalGreaterThanOne(),
        selectTotalAndBlogId()
    ), BlogUpdateFailedEvent.class, UpdateBlogFailureCount.class).getMappedResults();
  }

  private MatchOperation whereTotalGreaterThanOne() {
    return Aggregation.match(Criteria.where(TOTAL).gt(1L));
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