package pl.michal.olszewski.rssaggregator.blog.search.items;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "itemsSearch")
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
final class ItemForSearch {

  @Id
  private String id;
  @TextIndexed(weight = 2)
  private String title;
  @TextIndexed(weight = 1)
  private String description;
  @Indexed(unique = true)
  private String link;
}
