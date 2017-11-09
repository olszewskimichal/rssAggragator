package pl.michal.olszewski.rssaggregator.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"blog", "id"})
@NoArgsConstructor
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String title;
  @Column(length = 10000)
  private String description;
  private String link;
  private Instant date;
  private String author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blog_id")
  private Blog blog;

  public Item(ItemDTO itemDTO) {
    this.title = itemDTO.getTitle();
    this.description = itemDTO.getDescription();
    this.link = itemDTO.getLink();
    this.date = itemDTO.getDate();
    this.author = itemDTO.getAuthor();
  }
}
