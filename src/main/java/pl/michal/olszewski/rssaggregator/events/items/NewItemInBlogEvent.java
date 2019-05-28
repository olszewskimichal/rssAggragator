package pl.michal.olszewski.rssaggregator.events.items;

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

  @Id
  private String id;
  private final Instant occuredAt;
  private final String linkUrl;
  private final String linkTitle;
  private final String blogId;

  public NewItemInBlogEvent(Instant occuredAt, String linkUrl, String linkTitle, String blogId) {
    this.occuredAt = occuredAt;
    this.linkUrl = linkUrl;
    this.linkTitle = linkTitle;
    this.blogId = blogId;
  }
}
