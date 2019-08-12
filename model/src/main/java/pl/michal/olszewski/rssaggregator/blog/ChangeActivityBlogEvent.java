package pl.michal.olszewski.rssaggregator.blog;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;

@Getter
abstract class ChangeActivityBlogEvent implements Serializable {

  private final String blogId;
  private final Instant occurredAt;

  ChangeActivityBlogEvent(String blogId, Instant occurredAt) {
    this.blogId = blogId;
    this.occurredAt = occurredAt;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ChangeActivityBlogEvent)) {
      return false;
    }
    ChangeActivityBlogEvent that = (ChangeActivityBlogEvent) o;
    return Objects.equals(blogId, that.blogId) &&
        Objects.equals(occurredAt, that.occurredAt);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(blogId, occurredAt);
  }
}
