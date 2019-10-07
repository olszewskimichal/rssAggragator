package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;


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
  public int hashCode() {
    return Objects.hash(content, totalElements);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PageItemDTO)) {
      return false;
    }
    PageItemDTO that = (PageItemDTO) o;
    return totalElements == that.totalElements &&
        Objects.equals(content, that.content);
  }

  @Override
  public String toString() {
    return "PageItemDTO{" +
        "content=" + content +
        ", totalElements=" + totalElements +
        '}';
  }
}
