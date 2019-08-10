package pl.michal.olszewski.rssaggregator.blog;

import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;

@Getter
class ChangeActivityBlogEvent implements Serializable { //TODO TO chyba powinny byc commandy

  private final String blogId;
  private final Instant occurredAt;

  ChangeActivityBlogEvent(String blogId, Instant occurredAt) {
    this.blogId = blogId;
    this.occurredAt = occurredAt;
  }

}
