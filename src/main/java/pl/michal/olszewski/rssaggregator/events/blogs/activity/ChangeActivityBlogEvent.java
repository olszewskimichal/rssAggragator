package pl.michal.olszewski.rssaggregator.events.blogs.activity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
public class ChangeActivityBlogEvent implements Serializable {

  private final String blogId;
  private final Instant occurredAt;
  @Id
  private String id;

  ChangeActivityBlogEvent(String blogId, Instant occurredAt) {
    this.blogId = blogId;
    this.occurredAt = occurredAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChangeActivityBlogEvent that = (ChangeActivityBlogEvent) o;
    return Objects.equals(blogId, that.blogId) &&
        Objects.equals(occurredAt, that.occurredAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(blogId, occurredAt);
  }
}
