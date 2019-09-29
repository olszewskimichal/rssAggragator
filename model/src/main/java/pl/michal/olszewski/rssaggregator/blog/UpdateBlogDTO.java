package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import lombok.Builder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

final class UpdateBlogDTO {

  private final String link;
  private final String description;
  private final String name;
  private final String feedURL;
  private final Instant publishedDate;

  @Builder
  UpdateBlogDTO(
      String link,
      String description,
      String name,
      String feedURL,
      Instant publishedDate
  ) {
    this.link = link;
    this.description = description;
    this.name = name;
    this.feedURL = feedURL;
    this.publishedDate = publishedDate;
  }

  public String getLink() {
    return link;
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

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

}
