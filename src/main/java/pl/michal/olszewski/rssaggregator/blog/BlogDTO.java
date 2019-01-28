package pl.michal.olszewski.rssaggregator.blog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
class BlogDTO {
    private String link;
    private String description;
    private String name;
    private String feedURL;
    private Instant publishedDate;
    @Builder.Default
    @JsonProperty("itemsList")
    private List<ItemDTO> itemsList = new ArrayList<>();

    List<ItemDTO> getItemsList() {
        if (itemsList == null) {
            itemsList = new ArrayList<>();
        }
        return Collections.unmodifiableList(itemsList);
    }

    public BlogDTO(Blog blog, List<ItemDTO> items) {
        this.link = blog.getBlogURL();
        this.description = blog.getDescription();
        this.name = blog.getName();
        this.feedURL = blog.getFeedURL();
        this.publishedDate = blog.getPublishedDate();
        this.itemsList=items;
    }

    void addNewItem(ItemDTO itemDTO) {
        this.itemsList.add(itemDTO);
    }
}
