package pl.michal.olszewski.rssaggregator.blog;

public class CreateBlogDTOBuilder {

  private String link;
  private String description;
  private String name;
  private String feedURL;

  public CreateBlogDTOBuilder link(String link) {
    this.link = link;
    return this;
  }

  public CreateBlogDTOBuilder description(String description) {
    this.description = description;
    return this;
  }

  public CreateBlogDTOBuilder name(String name) {
    this.name = name;
    return this;
  }

  public CreateBlogDTOBuilder feedURL(String feedURL) {
    this.feedURL = feedURL;
    return this;
  }

  public CreateBlogDTO build() {
    return new CreateBlogDTO(link, description, name, feedURL);
  }
}