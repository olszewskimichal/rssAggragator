package pl.michal.olszewski.rssaggregator.blog;

import reactor.core.publisher.Mono;

interface ReactiveAggregationBlogRepository {

  Mono<BlogAggregationDTO> getBlogWithCount(String id);
}
