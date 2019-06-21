package pl.michal.olszewski.rssaggregator.newitem;

import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@EqualsAndHashCode
@Document
@ToString
public class NewItemInBlogEvent implements Serializable {

  private final Instant occurredAt;
  private final String linkUrl;
  private final String linkTitle;
  private final String blogId;
  @Id
  private String id;

  public NewItemInBlogEvent(Instant occurredAt, String linkUrl, String linkTitle, String blogId) {
    this.occurredAt = occurredAt;
    this.linkUrl = linkUrl;
    this.linkTitle = linkTitle;
    this.blogId = blogId;
  }
}