package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
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
}
