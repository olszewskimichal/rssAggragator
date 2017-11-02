package pl.michal.olszewski.rssaggregator.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
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

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Item> items = new HashSet<>();

    public Blog(String blogURL, String description, String name, String feedURL, Instant publishedDate) {
        this.blogURL = blogURL;
        this.description = description;
        this.name = name;
        this.feedURL = feedURL;
        this.publishedDate = publishedDate;
        this.items = new HashSet<>();
    }

    public Set<Item> getItems() {
        return Collections.unmodifiableSet(items);
    }

    public void addItem(Item item) {
        if (items.add(item))
            item.setBlog(this);
    }

    public void updateFromDto(BlogDTO blogDTO) {
        this.description = blogDTO.getDescription();
        this.name = blogDTO.getName();
        this.publishedDate = blogDTO.getPublishedDate();
    }
}