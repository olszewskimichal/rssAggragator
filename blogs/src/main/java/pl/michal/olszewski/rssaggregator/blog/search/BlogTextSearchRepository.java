package pl.michal.olszewski.rssaggregator.blog.search;

import reactor.core.publisher.Flux;

interface BlogTextSearchRepository {

  Flux<BlogSearchResult> findMatching(String searchValue, int limit);
}
