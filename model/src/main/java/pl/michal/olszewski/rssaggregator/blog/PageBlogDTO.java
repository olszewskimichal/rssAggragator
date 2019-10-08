package pl.michal.olszewski.rssaggregator.blog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

final class PageBlogDTO {

  private final List<BlogDTO> content;
  private final long totalElements;

  @JsonCreator
  public PageBlogDTO(
      @JsonProperty("content") List<BlogDTO> content,
      @JsonProperty("totalElements") long totalElements
  ) {
    this.content = content;
    this.totalElements = totalElements;
  }

  public List<BlogDTO> getContent() {
    return content;
  }

  public long getTotalElements() {
    return totalElements;
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
