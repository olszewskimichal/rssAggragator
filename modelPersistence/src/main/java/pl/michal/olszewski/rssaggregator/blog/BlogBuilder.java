package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;

public class BlogBuilder {

  private String id;
  private String blogURL;
  private String description;
  private String name;
  private String feedURL;
  private Instant lastUpdateDate;

  public BlogBuilder id(String id) {
    this.id = id;
    return this;
  }

  public BlogBuilder blogURL(String blogURL) {
    this.blogURL = blogURL;
    return this;
  }

  public BlogBuilder description(String description) {
    this.description = description;
    return this;
  }

  public BlogBuilder name(String name) {
    this.name = name;
    return this;
  }

  public BlogBuilder feedURL(String feedURL) {
    this.feedURL = feedURL;
    return this;
  }

  public BlogBuilder lastUpdateDate(Instant lastUpdateDate) {
    this.lastUpdateDate = lastUpdateDate;
    return this;
  }

  public Blog build() {
    return new Blog(id, blogURL, description, name, feedURL, null, lastUpdateDate, null);
  }
}