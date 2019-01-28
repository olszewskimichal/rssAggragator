package pl.michal.olszewski.rssaggregator.blog;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;

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

  void addNewItem(ItemDTO itemDTO) {
    this.itemsList.add(itemDTO);
  }
}
