package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ItemDTO implements Serializable {

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

  @Override
  public int hashCode() {
    return Objects.hash(title, description, link, date, author, blogId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ItemDTO)) {
      return false;
    }
    ItemDTO itemDTO = (ItemDTO) o;
    return Objects.equals(title, itemDTO.title) &&
        Objects.equals(description, itemDTO.description) &&
        Objects.equals(link, itemDTO.link) &&
        Objects.equals(date, itemDTO.date) &&
        Objects.equals(author, itemDTO.author) &&
        Objects.equals(blogId, itemDTO.blogId);
  }

  @Override
  public String toString() {
    return "ItemDTO{" +
        "title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", link='" + link + '\'' +
        ", date=" + date +
        ", author='" + author + '\'' +
        ", blogId='" + blogId + '\'' +
        '}';
  }
}
