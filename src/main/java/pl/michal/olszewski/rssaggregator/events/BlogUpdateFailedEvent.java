package pl.michal.olszewski.rssaggregator.events;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlogUpdateFailedEvent extends EventBase {

  private final Instant occurredAt;
  private final String correlationId;
  private final String blogUrl;
  private final String errorMsg;

  public BlogUpdateFailedEvent(Instant occurredAt, String correlationId, String blogUrl, String errorMsg) {
    this.occurredAt = occurredAt;
    this.correlationId = correlationId;
    this.blogUrl = blogUrl;
    this.errorMsg = errorMsg;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public String getBlogUrl() {
    return blogUrl;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  @Override
  public String toString() {
    return "BlogUpdateFailedByTimeoutEvent{" +
        ", occurredAt=" + occurredAt +
        ", correlationId='" + correlationId + '\'' +
        ", blogUrl='" + blogUrl + '\'' +
        ", errorMsg='" + errorMsg + '\'' +
        '}';
  }
}
