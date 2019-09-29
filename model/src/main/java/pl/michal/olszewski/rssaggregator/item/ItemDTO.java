package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDTO implements Serializable {

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

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getLink() {
    return link;
  }

  public Instant getDate() {
    return date;
  }

  public String getAuthor() {
    return author;
  }

  public String getBlogId() {
    return blogId;
  }
}
