package pl.michal.olszewski.rssaggregator.blog.search.items;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class ItemTextSearchRepositoryImpl implements ItemTextSearchRepository {

  private final ReactiveMongoTemplate mongoTemplate;

  public ItemTextSearchRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Flux<ItemSearchResult> findMatching(String searchValue, int limit) {
    TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matching(searchValue);
    Query query = TextQuery.queryText(textCriteria)
        .sortByScore()
        .with(PageRequest.of(0, limit));
    return mongoTemplate.find(query, ItemSearchResult.class, "item");
  }
}