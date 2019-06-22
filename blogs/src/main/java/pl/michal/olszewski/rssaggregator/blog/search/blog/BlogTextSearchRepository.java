package pl.michal.olszewski.rssaggregator.blog.search.blog;

import reactor.core.publisher.Flux;

interface BlogTextSearchRepository {

  Flux<BlogSearchResult> findMatching(String searchValue, int limit);
}
