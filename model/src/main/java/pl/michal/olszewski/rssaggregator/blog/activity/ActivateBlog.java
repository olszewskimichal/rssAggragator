package pl.michal.olszewski.rssaggregator.blog.activity;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
class ActivateBlog extends ChangeActivityBlogEvent {

  @Builder
  public ActivateBlog(String blogId, Instant occurredAt) {
    super(blogId, occurredAt);
  }
}