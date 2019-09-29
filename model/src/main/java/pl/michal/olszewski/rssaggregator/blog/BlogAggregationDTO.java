package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.util.Optional;
import lombok.Builder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class BlogAggregationDTO {

  private final String blogId;
  private final String link;
  private final String description;
  private final String name;
  private final String feedURL;
  private final Instant publishedDate;
  private final Long blogItemsCount;

  @Builder
  public BlogAggregationDTO(String blogId, String link, String description, String name, String feedURL, Instant publishedDate, Long blogItemsCount) {
    this.blogId = blogId;
    this.link = link;
    this.description = description;
    this.name = name;
    this.feedURL = feedURL;
    this.publishedDate = publishedDate;
    this.blogItemsCount = Optional.ofNullable(blogItemsCount).orElse(0L);
  }

  public String getBlogId() {
    return blogId;
  }

  public String getLink() {
    return link;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public String getFeedURL() {
    return feedURL;
  }

  public Instant getPublishedDate() {
    return publishedDate;
  }

  public Long getBlogItemsCount() {
    return blogItemsCount;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

}
