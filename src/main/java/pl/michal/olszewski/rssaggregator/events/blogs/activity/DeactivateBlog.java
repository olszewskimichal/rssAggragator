package pl.michal.olszewski.rssaggregator.events.blogs.activity;

import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class DeactivateBlog implements Serializable {

  private final String blogId;
  private final Instant occurredAt;

  @Builder
  public DeactivateBlog(String blogId, Instant occurredAt) {
    this.blogId = blogId;
    this.occurredAt = occurredAt;
  }
}
