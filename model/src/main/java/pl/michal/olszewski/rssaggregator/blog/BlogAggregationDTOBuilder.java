package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;

public class BlogAggregationDTOBuilder {

  private String blogId;
  private String link;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;
  private Long blogItemsCount;

  public BlogAggregationDTOBuilder blogId(String blogId) {
    this.blogId = blogId;
    return this;
  }

  public BlogAggregationDTOBuilder link(String link) {
    this.link = link;
    return this;
  }

  public BlogAggregationDTOBuilder description(String description) {
    this.description = description;
    return this;
  }

  public BlogAggregationDTOBuilder name(String name) {
    this.name = name;
    return this;
  }

  public BlogAggregationDTOBuilder feedURL(String feedURL) {
    this.feedURL = feedURL;
    return this;
  }

  public BlogAggregationDTOBuilder publishedDate(Instant publishedDate) {
    this.publishedDate = publishedDate;
    return this;
  }

  public BlogAggregationDTOBuilder blogItemsCount(Long blogItemsCount) {
    this.blogItemsCount = blogItemsCount;
    return this;
  }

  public BlogAggregationDTO build() {
    return new BlogAggregationDTO(blogId, link, description, name, feedURL, publishedDate, blogItemsCount);
  }
}