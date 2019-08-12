package pl.michal.olszewski.rssaggregator.blog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
final class CreateBlogDTO {

  private final String link;
  private final String description;
  private final String name;
  private final String feedURL;

  @Builder
  @JsonCreator
  CreateBlogDTO(
      @JsonProperty("link") String link,
      @JsonProperty("description") String description,
      @JsonProperty("name") String name,
      @JsonProperty("feedURL") String feedURL
  ) {
    this.link = link;
    this.description = description;
    this.name = name;
    this.feedURL = feedURL;
  }

  @Override
  public int hashCode() {
    return Objects.hash(link, description, name, feedURL);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateBlogDTO that = (CreateBlogDTO) o;
    return Objects.equals(link, that.link) &&
        Objects.equals(description, that.description) &&
        Objects.equals(name, that.name) &&
        Objects.equals(feedURL, that.feedURL);
  }

  @Override
  public String toString() {
    return "CreateBlogDTO{" +
        "link='" + link + '\'' +
        ", description='" + description + '\'' +
        ", name='" + name + '\'' +
        ", feedURL='" + feedURL + '\'' +
        '}';
  }
}
