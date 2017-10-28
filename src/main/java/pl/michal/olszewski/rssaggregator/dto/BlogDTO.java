package pl.michal.olszewski.rssaggregator.dto;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class BlogDTO {
    private final String link;
    private final String description;
    private final String name;
    private final String feedURL;
    private final Instant publishedDate;
    @Builder.Default
    private List<ItemDTO> itemsList = new ArrayList<>();

    public BlogDTO(String link, String description, String name, String feedURL, Instant publishedDate, List<ItemDTO> itemsList) {
        this.link = link;
        this.description = description;
        this.name = name;
        this.feedURL = feedURL;
        this.publishedDate = publishedDate;
        this.itemsList = itemsList;
    }

    public List<ItemDTO> getItemsList() {
        if (itemsList == null) itemsList = new ArrayList<>();
        return Collections.unmodifiableList(itemsList);
    }

    public void addNewItem(ItemDTO itemDTO) {
        this.itemsList.add(itemDTO);
    }
}
