package pl.michal.olszewski.rssaggregator.blog;

import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class BlogAggregationDTO {

  private final String blogId;
  private final Long blogItemsCount;

  BlogAggregationDTO(String blogId, Long blogItemsCount) {
    this.blogId = blogId;
    this.blogItemsCount = Optional.ofNullable(blogItemsCount).orElse(0L);
  }

  public String getBlogId() {
    return blogId;
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
