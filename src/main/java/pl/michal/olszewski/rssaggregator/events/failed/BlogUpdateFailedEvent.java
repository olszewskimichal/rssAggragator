package pl.michal.olszewski.rssaggregator.events.failed;

import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@EqualsAndHashCode
@ToString(exclude = "stackTrace")
public class BlogUpdateFailedEvent implements Serializable {

  private final String stackTrace;
  private final Instant occurredAt;
  private final String correlationId;
  private final String blogUrl;
  private final String blogId;
  private final String errorMsg;
  @Id
  private String id;

  public BlogUpdateFailedEvent(Instant occurredAt, String correlationId, String blogUrl, String blogId, String errorMsg, String stackTrace) {
    this.occurredAt = occurredAt;
    this.correlationId = correlationId;
    this.blogUrl = blogUrl;
    this.blogId = blogId;
    this.errorMsg = errorMsg;
    this.stackTrace = stackTrace;
  }
}
