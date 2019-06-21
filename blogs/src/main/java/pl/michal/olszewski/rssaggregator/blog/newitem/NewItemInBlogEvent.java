package pl.michal.olszewski.rssaggregator.blog.newitem;

import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@EqualsAndHashCode
@Document
@ToString
class NewItemInBlogEvent implements Serializable {

  private final Instant occurredAt;
  private final String linkUrl;
  private final String linkTitle;
  private final String blogId;

  public NewItemInBlogEvent(Instant occurredAt, String linkUrl, String linkTitle, String blogId) {
    this.occurredAt = occurredAt;
    this.linkUrl = linkUrl;
    this.linkTitle = linkTitle;
    this.blogId = blogId;
  }
}