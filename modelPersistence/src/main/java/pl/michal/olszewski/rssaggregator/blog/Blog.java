package pl.michal.olszewski.rssaggregator.blog;

import static org.springframework.util.StringUtils.isEmpty;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.michal.olszewski.rssaggregator.blog.ogtags.OgTagBlogInfo;

@Document
public class Blog {

  @Id
  private String id;
  @Indexed(unique = true)
  private String blogURL;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;
  private Instant lastUpdateDate;
  private String imageUrl;
  private boolean active = true;

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

  public Blog() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBlogURL() {
    return blogURL;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public String getFeedURL() {
    return feedURL;
  }

  public Instant getPublishedDate() {
    return publishedDate;
  }

  public String getImageUrl() {
    return imageUrl;
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

  void updateFromDto(UpdateBlogDTO blogDTO) {
    this.description = blogDTO.getDescription();
    this.name = blogDTO.getName();
    this.publishedDate = blogDTO.getPublishedDate();
    this.lastUpdateDate = Instant.now().minus(2, ChronoUnit.DAYS);
  }

  void activate() {
    active = true;
  }

  @Override
  public final int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this,"id");
  }

  @Override
  public final boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o,"id");
  }
}
