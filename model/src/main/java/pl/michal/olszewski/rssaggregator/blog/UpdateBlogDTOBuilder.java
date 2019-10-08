package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;

public class UpdateBlogDTOBuilder {

  private String link;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;

  public UpdateBlogDTOBuilder link(String link) {
    this.link = link;
    return this;
  }

  public UpdateBlogDTOBuilder description(String description) {
    this.description = description;
    return this;
  }

  public UpdateBlogDTOBuilder name(String name) {
    this.name = name;
    return this;
  }

  public UpdateBlogDTOBuilder feedURL(String feedURL) {
    this.feedURL = feedURL;
    return this;
  }

  public UpdateBlogDTOBuilder publishedDate(Instant publishedDate) {
    this.publishedDate = publishedDate;
    return this;
  }

  public UpdateBlogDTO build() {
    return new UpdateBlogDTO(link, description, name, feedURL, publishedDate);
  }
}