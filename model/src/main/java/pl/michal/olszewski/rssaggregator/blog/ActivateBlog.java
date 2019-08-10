package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
final class ActivateBlog extends ChangeActivityBlogEvent {

  @Builder
  public ActivateBlog(String blogId, Instant occurredAt) {
    super(blogId, occurredAt);
  }
}
