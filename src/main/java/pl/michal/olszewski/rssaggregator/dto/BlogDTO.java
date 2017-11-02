package pl.michal.olszewski.rssaggregator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
public class BlogDTO {
    private String link;
    private String description;
    private String name;
    private String feedURL;
    private Instant publishedDate;
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
