package pl.michal.olszewski.rssaggregator.search.items;

import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
class ItemSearchService {

  private final ReactiveElasticsearchOperations reactiveElasticsearchTemplate;

  ItemSearchService(ReactiveElasticsearchOperations reactiveElasticsearchTemplate) {
    this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
  }

  void saveItemForSearch(ItemForSearch itemForSearch) {
    reactiveElasticsearchTemplate.save(itemForSearch)
        .then()
        .block();
  }
}
