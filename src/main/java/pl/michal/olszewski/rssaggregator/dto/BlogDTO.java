package pl.michal.olszewski.rssaggregator.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class BlogDTO {
    private final String link;
    private final String description;
    private final String name;
    private final String feedURL;
    private final Instant publishedDate;
    private List<ItemDTO> itemsList;

    public BlogDTO(String link, String description, String name, String feedURL, Instant publishedDate) {
        this.link = link;
        this.description = description;
        this.name = name;
        this.feedURL = feedURL;
        this.publishedDate = publishedDate;
    }
}
