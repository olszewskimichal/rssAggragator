package pl.michal.olszewski.rssaggregator.item;

import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@ToString
@NoArgsConstructor
public final class Item {

  @Id
  private String id;
  private String title;
  private String description;
  @Indexed(unique = true)
  private String link;
  private Instant date;
  private String author;
  private boolean read;

  @CreatedDate
  private Instant createdAt;

  public Item(ItemDTO itemDTO) {
    this.title = itemDTO.getTitle();
    this.description = itemDTO.getDescription();
    this.link = itemDTO.getLink();
    this.date = itemDTO.getDate();
    this.author = itemDTO.getAuthor();
    this.read = false;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Item)) {
      return false;
    }
    Item item = (Item) o;
    return
        Objects.equals(title, item.title) &&
            Objects.equals(description, item.description) &&
            Objects.equals(link, item.link) &&
            Objects.equals(date, item.date) &&
            Objects.equals(author, item.author);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(title, description, link, date, author);
  }

  Item markAsRead() {
    this.read = true;
    return this;
  }

  Item markAsUnread() {
    this.read = false;
    return this;
  }
}
