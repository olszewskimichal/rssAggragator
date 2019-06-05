package pl.michal.olszewski.rssaggregator.events.blogs.activity;

import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class DeactivateBlog extends ChangeActivityBlogEvent {
  @Builder
  public DeactivateBlog(String blogId, Instant occurredAt) {
    super(blogId, occurredAt);
  }

}
