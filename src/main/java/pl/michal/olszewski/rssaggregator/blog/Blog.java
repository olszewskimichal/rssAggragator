package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.michal.olszewski.rssaggregator.item.Item;

@Document
@Getter
@NoArgsConstructor
@Slf4j
@ToString
public class Blog {

  @Id
  private String id;
  @Indexed(unique = true)
  private String blogURL;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;
  private Instant lastUpdateDate;
  private boolean active = true;
  @DBRef
  private Set<Item> items = new HashSet<>();

  public Blog(String blogURL, String description, String name, String feedURL, Instant publishedDate, Instant lastUpdateDate) {
    this.blogURL = blogURL;
    this.description = description;
    this.name = name;
    this.feedURL = feedURL;
    this.publishedDate = publishedDate;
    this.items = new HashSet<>();
    this.lastUpdateDate = lastUpdateDate;
  }

  public Blog(BlogDTO blogDTO) {
    this.blogURL = blogDTO.getLink();
    this.description = blogDTO.getDescription();
    this.name = blogDTO.getName();
    this.feedURL = blogDTO.getFeedURL();
    this.publishedDate = blogDTO.getPublishedDate();
  }

  public Set<Item> getItems() {
    return Collections.unmodifiableSet(items);
  }

  public void addItem(Item item, MongoTemplate repository) {
    if (items.add(item)) {
      repository.save(item);
      log.trace("Dodaje nowy wpis do bloga {} o tytule {} z linkiem {}", this.getName(), item.getTitle(), item.getLink());
    }
  }

  void updateFromDto(BlogDTO blogDTO) {
    this.description = blogDTO.getDescription();
    this.name = blogDTO.getName();
    this.publishedDate = blogDTO.getPublishedDate();
    this.lastUpdateDate = Instant.now().minus(2, ChronoUnit.DAYS);
  }

  boolean isActive() {
    return active;
  }

  void deactive() {
    active = false;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Blog)) {
      return false;
    }
    Blog blog = (Blog) o;
    return active == blog.active &&
        Objects.equals(blogURL, blog.blogURL) &&
        Objects.equals(description, blog.description) &&
        Objects.equals(name, blog.name) &&
        Objects.equals(feedURL, blog.feedURL) &&
        Objects.equals(publishedDate, blog.publishedDate) &&
        Objects.equals(lastUpdateDate, blog.lastUpdateDate) &&
        Objects.equals(items, blog.items);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(blogURL, description, name, feedURL, publishedDate, lastUpdateDate, active, items);
  }
}
