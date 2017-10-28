package pl.michal.olszewski.rssaggregator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;
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
