package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDTO {

  private final String title;
  private final String description;
  private final String link;
  private final Instant date;
  private final String author;
  private final String blogId;


  @JsonCreator
  public ItemDTO(
      @JsonProperty("title") String title,
      @JsonProperty("description") String description,
      @JsonProperty("link") String link,
      @JsonProperty("date") Instant date,
      @JsonProperty("author") String author,
      @JsonProperty("blogId") String blogId) {
    this.title = title;
    this.description = description;
    this.link = link;
    this.date = date;
    this.author = author;
    this.blogId = blogId;
  }

  public ItemDTO(Item item) {
    this.title = item.getTitle();
    this.description = item.getDescription();
    this.link = item.getLink();
    this.date = item.getDate();
    this.author = item.getAuthor();
    this.blogId = item.getBlogId();
  }
}
