package pl.michal.olszewski.rssaggregator.search.items;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class ItemSearchService {

  private final ReactiveElasticsearchOperations reactiveElasticsearchTemplate;

  ItemSearchService(ReactiveElasticsearchOperations reactiveElasticsearchTemplate) {
    this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
  }

  void saveItemForSearch(ItemForSearch itemForSearch) {
    log.debug("save item for search link {}", itemForSearch.getLink());
    reactiveElasticsearchTemplate.save(itemForSearch)
        .then()
        .block();
  }
}
