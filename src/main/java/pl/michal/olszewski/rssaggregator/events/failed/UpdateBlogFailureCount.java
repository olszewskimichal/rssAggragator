package pl.michal.olszewski.rssaggregator.events.failed;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
final class UpdateBlogFailureCount {

  private final String blogId;
  private final String errorMsg;
  private final Long total;

  UpdateBlogFailureCount(String blogId, String errorMsg, Long total) {
    this.blogId = blogId;
    this.errorMsg = errorMsg;
    this.total = total;
  }

}
