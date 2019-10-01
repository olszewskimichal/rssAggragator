package pl.michal.olszewski.rssaggregator.item;

import java.util.Objects;

public final class BlogItemLink {

  private final String blogId;
  private final String linkUrl;

  public BlogItemLink(String blogId, String linkUrl) {
    this.blogId = blogId;
    this.linkUrl = linkUrl;
  }

  @Override
  public int hashCode() {
    return Objects.hash(blogId, linkUrl);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlogItemLink that = (BlogItemLink) o;
    return Objects.equals(blogId, that.blogId) &&
        Objects.equals(linkUrl, that.linkUrl);
  }

  @Override
  public String toString() {
    return "BlogItemLink{" +
        "blogId='" + blogId + '\'' +
        ", linkUrl='" + linkUrl + '\'' +
        '}';
  }
}
