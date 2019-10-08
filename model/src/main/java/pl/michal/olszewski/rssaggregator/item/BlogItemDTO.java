package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
final class BlogItemDTO {

  private final String id;
  private final String title;
  private final String link;
  private final Instant date;
  private final String author;

  public BlogItemDTO(
      @JsonProperty("id") String id,
      @JsonProperty("title") String title,
      @JsonProperty("link") String link,
      @JsonProperty("date") Instant date,
      @JsonProperty("author") String author
  ) {
    this.id = id;
    this.title = title;
    this.link = link;
    this.date = date;
    this.author = author;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getLink() {
    return link;
  }

  public Instant getDate() {
    return date;
  }

  public String getAuthor() {
    return author;
  }

  @Override
  public final int hashCode() {
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
