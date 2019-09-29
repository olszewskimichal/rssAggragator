package pl.michal.olszewski.rssaggregator.blog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
