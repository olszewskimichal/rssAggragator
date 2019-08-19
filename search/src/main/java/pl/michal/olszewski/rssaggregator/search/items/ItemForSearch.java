package pl.michal.olszewski.rssaggregator.search.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "searchitems", refreshInterval = "-1")
@Builder
@Getter
final class ItemForSearch {

  @Field(type = FieldType.Text, fielddata = true, store = true)
  private final String title;
  private final String description;
  @Id
  private final String link;
  private final float score;

  @JsonCreator
  public ItemForSearch(
      @JsonProperty("title") String title,
      @JsonProperty("description") String description,
      @JsonProperty("link") String link,
      @JsonProperty("score") float score
  ) {
    this.title = title;
    this.description = description;
    this.link = link;
    this.score = score;
  }
}
