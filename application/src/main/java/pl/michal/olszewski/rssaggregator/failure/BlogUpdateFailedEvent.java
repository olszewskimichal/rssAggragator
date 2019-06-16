package pl.michal.olszewski.rssaggregator.failure;

import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class BlogUpdateFailedEvent implements Serializable {

  private final Instant occurredAt;
  private final String correlationId;
  private final String blogUrl;
  private final String blogId;
  private final String errorMsg;

  @Builder
  public BlogUpdateFailedEvent(Instant occurredAt, String correlationId, String blogUrl, String blogId, String errorMsg) {
    this.occurredAt = occurredAt;
    this.correlationId = correlationId;
    this.blogUrl = blogUrl;
    this.blogId = blogId;
    this.errorMsg = errorMsg;
  }
}
