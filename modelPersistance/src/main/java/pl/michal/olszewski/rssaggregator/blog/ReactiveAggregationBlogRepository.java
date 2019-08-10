package pl.michal.olszewski.rssaggregator.blog;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface ReactiveAggregationBlogRepository {

  Flux<BlogAggregationDTO> getBlogsWithCount();

  Mono<BlogAggregationDTO> getBlogWithCount(String id);
}
