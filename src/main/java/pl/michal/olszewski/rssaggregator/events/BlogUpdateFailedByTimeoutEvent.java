package pl.michal.olszewski.rssaggregator.events;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlogUpdateFailedByTimeoutEvent extends EventBase {

  private final Instant occurredAt;
  private final String correlationId;
  private final String blogId;
  private final String errorMsg;

  public BlogUpdateFailedByTimeoutEvent(Instant occurredAt, String correlationId, String blogId, String errorMsg) {
    this.occurredAt = occurredAt;
    this.correlationId = correlationId;
    this.blogId = blogId;
    this.errorMsg = errorMsg;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public String getBlogId() {
    return blogId;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  @Override
  public String toString() {
    return "BlogUpdateFailedByTimeoutEvent{" +
        ", occurredAt=" + occurredAt +
        ", correlationId='" + correlationId + '\'' +
        ", blogId='" + blogId + '\'' +
        ", errorMsg='" + errorMsg + '\'' +
        '}';
  }
}
