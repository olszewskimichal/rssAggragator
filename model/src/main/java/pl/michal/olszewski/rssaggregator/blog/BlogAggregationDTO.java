package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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

  @Override
  public int hashCode() {
    return Objects.hash(blogId, link, description, name, feedURL, publishedDate, blogItemsCount);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlogAggregationDTO that = (BlogAggregationDTO) o;
    return Objects.equals(blogId, that.blogId) &&
        Objects.equals(link, that.link) &&
        Objects.equals(description, that.description) &&
        Objects.equals(name, that.name) &&
        Objects.equals(feedURL, that.feedURL) &&
        Objects.equals(publishedDate, that.publishedDate) &&
        Objects.equals(blogItemsCount, that.blogItemsCount);
  }
}
