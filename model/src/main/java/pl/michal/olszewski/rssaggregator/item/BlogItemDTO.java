package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
class BlogItemDTO {

  private final String id;
  private final String title;
  private final String link;
  private final Instant date;
  private final String author;

  public BlogItemDTO(
      @JsonProperty("id") String id,
      @JsonProperty("title") String title,
      @JsonProperty("link") String link,
      @JsonProperty("date") Instant date,
      @JsonProperty("author") String author
  ) {
    this.id = id;
    this.title = title;
    this.link = link;
    this.date = date;
    this.author = author;
  }
}
