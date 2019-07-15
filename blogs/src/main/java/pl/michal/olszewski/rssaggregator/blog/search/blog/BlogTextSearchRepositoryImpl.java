package pl.michal.olszewski.rssaggregator.blog.search.blog;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class BlogTextSearchRepositoryImpl implements BlogTextSearchRepository {

  private final ReactiveMongoTemplate mongoTemplate;

  public BlogTextSearchRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Flux<BlogSearchResult> findMatching(String searchValue, int limit) {
    TextCriteria textCriteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matching(searchValue);
    Query query = TextQuery.queryText(textCriteria)
        .sortByScore()
        .with(PageRequest.of(0, limit));
    return mongoTemplate.find(query, BlogSearchResult.class, "blog");
  }
}
