package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


@JsonIgnoreProperties(ignoreUnknown = true)
public final class PageItemDTO implements Serializable {

  private final List<ItemDTO> content;
  private final long totalElements;

  @JsonCreator
  public PageItemDTO(
      @JsonProperty("content") List<ItemDTO> content,
      @JsonProperty("totalElements") long totalElements
  ) {
    this.content = content;
    this.totalElements = totalElements;
  }

  public List<ItemDTO> getContent() {
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
    return "PageItemDTO{" +
        "content=" + content +
        ", totalElements=" + totalElements +
        '}';
  }
}
