package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
class BlogAggregationDTO {

  private String id;
  private String link;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;
  private Long blogItemsCount;


  BlogAggregationDTO(Blog blog) {
    this.id = blog.getId();
    this.link = blog.getBlogURL();
    this.description = blog.getDescription();
    this.name = blog.getName();
    this.feedURL = blog.getFeedURL();
    this.publishedDate = blog.getPublishedDate();
    this.blogItemsCount = (long) blog.getItems().size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlogAggregationDTO that = (BlogAggregationDTO) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(link, that.link) &&
        Objects.equals(description, that.description) &&
        Objects.equals(name, that.name) &&
        Objects.equals(feedURL, that.feedURL) &&
        Objects.equals(publishedDate, that.publishedDate) &&
        Objects.equals(blogItemsCount, that.blogItemsCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, link, description, name, feedURL, publishedDate, blogItemsCount);
  }
}
