package pl.michal.olszewski.rssaggregator.blog.activity;

import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
class ChangeActivityBlogEvent implements Serializable {

  private final String blogId;
  private final Instant occurredAt;

  ChangeActivityBlogEvent(String blogId, Instant occurredAt) {
    this.blogId = blogId;
    this.occurredAt = occurredAt;
  }

}