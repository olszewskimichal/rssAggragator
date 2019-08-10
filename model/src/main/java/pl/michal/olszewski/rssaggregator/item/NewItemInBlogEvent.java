package pl.michal.olszewski.rssaggregator.item;

import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class NewItemInBlogEvent implements Serializable {

  private final Instant occurredAt;
  private final ItemDTO itemDTO;
  private final String blogId;

  public NewItemInBlogEvent(Instant occurredAt, ItemDTO itemDTO, String blogId) {
    this.occurredAt = occurredAt;
    this.itemDTO = itemDTO;
    this.blogId = blogId;
  }
}
