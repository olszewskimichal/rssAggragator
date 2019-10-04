package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.util.Objects;
import lombok.Builder;

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

  String getLink() {
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
    return Objects.hash(link, description, name, feedURL, publishedDate);
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateBlogDTO that = (UpdateBlogDTO) o;
    return Objects.equals(link, that.link) &&
        Objects.equals(description, that.description) &&
        Objects.equals(name, that.name) &&
        Objects.equals(feedURL, that.feedURL) &&
        Objects.equals(publishedDate, that.publishedDate);
  }

}
