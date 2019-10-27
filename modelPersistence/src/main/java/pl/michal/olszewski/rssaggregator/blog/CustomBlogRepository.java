package pl.michal.olszewski.rssaggregator.blog;

import java.util.Optional;

interface CustomBlogRepository {

  Optional<BlogAggregationDTO> getBlogWithCount(String id);
}
