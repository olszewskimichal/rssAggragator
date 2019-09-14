package pl.michal.olszewski.rssaggregator.search.items;

import reactor.core.publisher.Flux;

interface ItemTextSearchRepository {

  Flux<ItemSearchResult> findMatching(String searchValue, int limit);
}
