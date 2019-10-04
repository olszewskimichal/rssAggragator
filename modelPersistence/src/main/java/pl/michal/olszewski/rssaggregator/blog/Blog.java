package pl.michal.olszewski.rssaggregator.blog;

import static org.springframework.util.StringUtils.isEmpty;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.michal.olszewski.rssaggregator.blog.ogtags.OgTagBlogInfo;

@Document
@Getter
@NoArgsConstructor
@Slf4j
public class Blog {

  @Id
  @Setter
  private String id;
  @Indexed(unique = true)
  private String blogURL;
  @TextIndexed(weight = 1)
  private String description;
  @TextIndexed(weight = 2)
  private String name;
  private String feedURL;
  private Instant publishedDate;
  private Instant lastUpdateDate;
  private String imageUrl;
  private boolean active = true;

  @Builder
  public Blog(
      String id,
      String blogURL,
      String description,
      String name,
      String feedURL,
      Instant publishedDate,
      Instant lastUpdateDate,
      String imageUrl
  ) {
    this.id = id;
    this.blogURL = blogURL;
    this.description = description;
    this.name = name;
    this.feedURL = feedURL;
    this.publishedDate = publishedDate;
    this.lastUpdateDate = lastUpdateDate;
    this.imageUrl = imageUrl;
  }

  public Blog(CreateBlogDTO blogDTO) {
    this.blogURL = blogDTO.getLink();
    this.description = blogDTO.getDescription();
    this.name = blogDTO.getName();
    this.feedURL = blogDTO.getFeedURL();
  }

  public boolean isActive() {
    return active;
  }

  public void deactivate() {
    active = false;
  }

  public RssInfo getRssInfo() {
    return new RssInfo(feedURL, blogURL, id, lastUpdateDate);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(blogURL, description, name, feedURL, publishedDate, lastUpdateDate, active, imageUrl);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Blog)) {
      return false;
    }
    Blog blog = (Blog) o;
    return active == blog.active &&
        Objects.equals(blogURL, blog.blogURL) &&
        Objects.equals(description, blog.description) &&
        Objects.equals(name, blog.name) &&
        Objects.equals(feedURL, blog.feedURL) &&
        Objects.equals(publishedDate, blog.publishedDate) &&
        Objects.equals(imageUrl, blog.imageUrl) &&
        Objects.equals(lastUpdateDate, blog.lastUpdateDate);
  }

  void updateFromDto(UpdateBlogDTO blogDTO) {
    this.description = blogDTO.getDescription();
    this.name = blogDTO.getName();
    this.publishedDate = blogDTO.getPublishedDate();
    this.lastUpdateDate = Instant.now().minus(2, ChronoUnit.DAYS);
  }

  void activate() {
    active = true;
  }

  public void updateBlogByOgTagInfo(OgTagBlogInfo blogInfo) {
    if (isEmpty(name) && blogInfo.getTitle() != null) {
      name = blogInfo.getTitle();
    }
    if (isEmpty(description) && blogInfo.getDescription() != null) {
      description = blogInfo.getDescription();
    }
    if (blogInfo.getImageUrl() != null) {
      imageUrl = blogInfo.getImageUrl();
    }
  }
}
