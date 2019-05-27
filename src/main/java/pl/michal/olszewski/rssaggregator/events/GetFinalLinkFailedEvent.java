package pl.michal.olszewski.rssaggregator.events;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public
class GetFinalLinkFailedEvent extends EventBase {

  private final Instant occurredAt;
  private final String errorMsg;

  public GetFinalLinkFailedEvent(Instant occurredAt, String errorMsg) {
    this.occurredAt = occurredAt;
    this.errorMsg = errorMsg;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  @Override
  public String toString() {
    return "GetFinalLinkFailedEvent{" +
        ", occurredAt=" + occurredAt +
        ", errorMsg='" + errorMsg + '\'' +
        '}';
  }
}
