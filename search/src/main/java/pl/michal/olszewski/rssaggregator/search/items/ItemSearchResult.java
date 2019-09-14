package pl.michal.olszewski.rssaggregator.search.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class ItemSearchResult {

  private final String title;
  private final String description;
  private final String link;
  private final String score;

  @JsonCreator
  ItemSearchResult(
      @JsonProperty("title") String title,
      @JsonProperty("description") String description,
      @JsonProperty("link") String link,
      @JsonProperty("score") String score
  ) {
    this.title = title;
    this.description = description;
    this.link = link;
    this.score = score;
  }
}
