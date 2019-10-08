package pl.michal.olszewski.rssaggregator.item;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class BlogItemLink {

  private final String blogId;
  private final String linkUrl;

  public BlogItemLink(String blogId, String linkUrl) {
    this.blogId = blogId;
    this.linkUrl = linkUrl;
  }

  public String getBlogId() {
    return blogId;
  }

  public String getLinkUrl() {
    return linkUrl;
  }

  @Override
  public final int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }
}
