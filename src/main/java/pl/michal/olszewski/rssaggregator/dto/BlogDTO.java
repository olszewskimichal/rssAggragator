package pl.michal.olszewski.rssaggregator.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
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
        this.itemsList=new ArrayList<>();
    }

    public List<ItemDTO> getItemsList() {
        return Collections.unmodifiableList(itemsList);
    }

    public void addNewItem(ItemDTO itemDTO){
        this.itemsList.add(itemDTO);
    }
}
