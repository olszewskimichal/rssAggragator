package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.michal.olszewski.rssaggregator.item.Item;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Slf4j
public class Blog {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  @Column(unique = true)
  private String blogURL;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;
  private Instant lastUpdateDate;
  private boolean active = true;

  @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
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

  public Set<Item> getItems() {
    return Collections.unmodifiableSet(items);
  }

  public void addItem(Item item) {
    if (items.add(item)) {
      log.trace("Dodaje nowy wpis do bloga {} o tytule {} z linkiem {}", this.getName(), item.getTitle(), item.getLink());
      item.setBlog(this);
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
}
