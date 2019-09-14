package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
class BlogItemDTO {

  @JsonProperty("title")
  private final String title;
  @JsonProperty("link")
  private final String link;
  @JsonProperty("date")
  private final Instant date;
  @JsonProperty("author")
  private final String author;

  BlogItemDTO(Item item) {
    this.title = item.getTitle();
    this.link = item.getLink();
    this.date = item.getDate();
    this.author = item.getAuthor();
  }
}
