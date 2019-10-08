package pl.michal.olszewski.rssaggregator.search.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
class ItemSearchService {

  private static final Logger log = LoggerFactory.getLogger(ItemSearchService.class);
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
