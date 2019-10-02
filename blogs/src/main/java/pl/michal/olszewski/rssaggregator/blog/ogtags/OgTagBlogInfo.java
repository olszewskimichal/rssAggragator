package pl.michal.olszewski.rssaggregator.blog.ogtags;

public class OgTagBlogInfo {

  private final String title;
  private final String description;
  private final String imageUrl;

  public OgTagBlogInfo(String title, String description, String imageUrl) {
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  @Override
  public String toString() {
    return "OgTagBlogInfo{" +
        "title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", imageUrl='" + imageUrl + '\'' +
        '}';
  }

  String getTitle() {
    return title;
  }

  String getDescription() {
    return description;
  }

  String getImageUrl() {
    return imageUrl;
  }
}
