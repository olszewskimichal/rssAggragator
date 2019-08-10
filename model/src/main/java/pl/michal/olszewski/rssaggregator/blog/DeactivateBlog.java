package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
final class DeactivateBlog extends ChangeActivityBlogEvent {

  @Builder
  public DeactivateBlog(String blogId, Instant occurredAt) {
    super(blogId, occurredAt);
  }

}
