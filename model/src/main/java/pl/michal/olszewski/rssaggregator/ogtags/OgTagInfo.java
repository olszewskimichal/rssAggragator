package pl.michal.olszewski.rssaggregator.ogtags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OgTagInfo {

  private final String title;
  private final String description;
  private final String imageUrl;

  public OgTagInfo(String title, String description, String imageUrl) {
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
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public final boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }
}
