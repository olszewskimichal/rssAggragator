package pl.michal.olszewski.rssaggregator.blog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
final class BlogDTO {

  private final String id;
  private final String link;
  private final String description;
  private final String name;
  private final String feedURL;
  private final Instant publishedDate;

  @Builder
  @JsonCreator
  BlogDTO(
      @JsonProperty("id") String id,
      @JsonProperty("link") String link,
      @JsonProperty("description") String description,
      @JsonProperty("name") String name,
      @JsonProperty("feedURL") String feedURL,
      @JsonProperty("publishedDate") Instant publishedDate
  ) {
    this.id = id;
    this.link = link;
    this.description = description;
    this.name = name;
    this.feedURL = feedURL;
    this.publishedDate = publishedDate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, link, description, name, feedURL, publishedDate);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlogDTO blogDTO = (BlogDTO) o;
    return Objects.equals(id, blogDTO.id) &&
        Objects.equals(link, blogDTO.link) &&
        Objects.equals(description, blogDTO.description) &&
        Objects.equals(name, blogDTO.name) &&
        Objects.equals(feedURL, blogDTO.feedURL) &&
        Objects.equals(publishedDate, blogDTO.publishedDate);
  }

  @Override
  public String toString() {
    return "BlogDTO{" +
        "id='" + id + '\'' +
        ", link='" + link + '\'' +
        ", description='" + description + '\'' +
        ", name='" + name + '\'' +
        ", feedURL='" + feedURL + '\'' +
        ", publishedDate=" + publishedDate +
        '}';
  }
}
