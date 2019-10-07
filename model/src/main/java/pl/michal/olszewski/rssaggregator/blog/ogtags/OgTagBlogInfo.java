package pl.michal.olszewski.rssaggregator.blog.ogtags;

import java.util.Objects;

public class OgTagBlogInfo {

  private final String title;
  private final String description;
  private final String imageUrl;

  public OgTagBlogInfo(String title, String description, String imageUrl) {
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(title, description, imageUrl);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OgTagBlogInfo)) {
      return false;
    }
    OgTagBlogInfo that = (OgTagBlogInfo) o;
    return Objects.equals(title, that.title) &&
        Objects.equals(description, that.description) &&
        Objects.equals(imageUrl, that.imageUrl);
  }

}
