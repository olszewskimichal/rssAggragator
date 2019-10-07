package pl.michal.olszewski.rssaggregator.blog;

import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class BlogAggregationDTO {

  private final String blogId;
  private final Long blogItemsCount;

  @Builder
  public BlogAggregationDTO(String blogId, Long blogItemsCount) {
    this.blogId = blogId;
    this.blogItemsCount = Optional.ofNullable(blogItemsCount).orElse(0L);
  }

  @Override
  public int hashCode() {
    return Objects.hash(blogId, blogItemsCount);
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
        Objects.equals(blogItemsCount, that.blogItemsCount);
  }
}
