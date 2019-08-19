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

  public BlogItemDTO(String title, String link, Instant date, String author) {
    this.title = title;
    this.link = link;
    this.date = date;
    this.author = author;
  }
}