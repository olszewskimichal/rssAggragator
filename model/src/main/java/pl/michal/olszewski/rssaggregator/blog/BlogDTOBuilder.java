package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;

public class BlogDTOBuilder {

  private String id;
  private String link;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;
  private String imageURL;

  public BlogDTOBuilder id(String id) {
    this.id = id;
    return this;
  }

  public BlogDTOBuilder link(String link) {
    this.link = link;
    return this;
  }

  public BlogDTOBuilder description(String description) {
    this.description = description;
    return this;
  }

  public BlogDTOBuilder name(String name) {
    this.name = name;
    return this;
  }

  public BlogDTOBuilder feedURL(String feedURL) {
    this.feedURL = feedURL;
    return this;
  }

  public BlogDTOBuilder publishedDate(Instant publishedDate) {
    this.publishedDate = publishedDate;
    return this;
  }

  public BlogDTOBuilder imageUrl(String imageURL) {
    this.imageURL = imageURL;
    return this;
  }

  public BlogDTO build() {
    return new BlogDTO(id, link, description, name, feedURL, publishedDate, imageURL);
  }
}