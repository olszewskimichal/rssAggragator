package pl.michal.olszewski.rssaggregator.blog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

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
    return Objects.hash(content, totalElements);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PageBlogDTO)) {
      return false;
    }
    PageBlogDTO that = (PageBlogDTO) o;
    return totalElements == that.totalElements &&
        Objects.equals(content, that.content);
  }

  @Override
  public String toString() {
    return "PageBlogDTO{" +
        "content=" + content +
        ", totalElements=" + totalElements +
        '}';
  }
}
