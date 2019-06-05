package pl.michal.olszewski.rssaggregator.events.blogs.activity;

import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class ActivateBlog implements Serializable {

  private final String blogId;
  private final Instant occurredAt;

  @Builder
  public ActivateBlog(String blogId, Instant occurredAt) {
    this.blogId = blogId;
    this.occurredAt = occurredAt;
  }
}
