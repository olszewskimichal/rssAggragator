package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
class BlogInfoDTO {

  private String id;
  private String link;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;

  BlogInfoDTO(Blog blog) {
    this.id = blog.getId();
    this.link = blog.getBlogURL();
    this.description = blog.getDescription();
    this.name = blog.getName();
    this.feedURL = blog.getFeedURL();
    this.publishedDate = blog.getPublishedDate();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlogInfoDTO blogDTO = (BlogInfoDTO) o;
    return Objects.equals(id, blogDTO.id) &&
        Objects.equals(link, blogDTO.link) &&
        Objects.equals(description, blogDTO.description) &&
        Objects.equals(name, blogDTO.name) &&
        Objects.equals(feedURL, blogDTO.feedURL) &&
        Objects.equals(publishedDate, blogDTO.publishedDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, link, description, name, feedURL, publishedDate);
  }
}
