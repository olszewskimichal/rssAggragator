package pl.michal.olszewski.rssaggregator.search.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "searchitems")
@Builder
@Getter
final class ItemForSearch {

  @Id
  private final String id;
  @Field(type = FieldType.Text, fielddata = true, store = true)
  private final String title;
  private final String description;
  private final String link;

  @JsonCreator
  public ItemForSearch(
      @JsonProperty("id") String id,
      @JsonProperty("title") String title,
      @JsonProperty("description") String description,
      @JsonProperty("link") String link
  ) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.link = link;
  }
}
