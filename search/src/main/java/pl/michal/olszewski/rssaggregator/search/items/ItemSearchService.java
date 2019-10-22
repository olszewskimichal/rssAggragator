package pl.michal.olszewski.rssaggregator.search.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class ItemSearchService {

  private static final Logger log = LoggerFactory.getLogger(ItemSearchService.class);
  private final ReactiveElasticsearchOperations reactiveElasticsearchTemplate;

  ItemSearchService(ReactiveElasticsearchOperations reactiveElasticsearchTemplate) {
    this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
  }

  Mono<ItemForSearch> saveItemForSearch(ItemForSearch itemForSearch) {
    log.debug("save item for search link {}", itemForSearch.getLink());
    return reactiveElasticsearchTemplate.save(itemForSearch);
  }
}
