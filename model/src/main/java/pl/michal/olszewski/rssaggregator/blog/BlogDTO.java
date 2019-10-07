package pl.michal.olszewski.rssaggregator.blog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

final class BlogDTO {

  private final String id;
  private final String link;
  private final String description;
  private final String name;
  private final String feedURL;
  private final Instant publishedDate;
  private final String imageURL;

  @JsonCreator
  BlogDTO(
      @JsonProperty("id") String id,
      @JsonProperty("link") String link,
      @JsonProperty("description") String description,
      @JsonProperty("name") String name,
      @JsonProperty("feedURL") String feedURL,
      @JsonProperty("publishedDate") Instant publishedDate,
      @JsonProperty("imageURL") String imageURL
  ) {
    this.id = id;
    this.link = link;
    this.description = description;
    this.name = name;
    this.feedURL = feedURL;
    this.publishedDate = publishedDate;
    this.imageURL = imageURL;
  }

  public String getId() {
    return id;
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

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
