package pl.michal.olszewski.rssaggregator.blog.search.items;

import reactor.core.publisher.Flux;

interface ItemTextSearchRepository {

  Flux<ItemSearchResult> findMatching(String searchValue, int limit);
}
