package pl.michal.olszewski.rssaggregator.search.items;

import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
class ItemTextSearchRepositoryImpl implements ItemTextSearchRepository {

  private final ReactiveElasticsearchOperations elasticsearchOperations;

  ItemTextSearchRepositoryImpl(ReactiveElasticsearchOperations elasticsearchOperations) {
    this.elasticsearchOperations = elasticsearchOperations;
  }

  @Override
  public Flux<ItemSearchResult> findMatching(String searchValue, int limit) {
    MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(searchValue)
        .field("title", 2.0f)
        .field("description");

    NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
        .withQuery(multiMatchQueryBuilder)
        .withPageable(PageRequest.of(0, limit))
        .build();
    return elasticsearchOperations.find(searchQuery, ItemForSearch.class)
        .map(itemForSearch -> new ItemSearchResult(itemForSearch.getTitle(), itemForSearch.getDescription(), itemForSearch.getLink()));
  }
}
