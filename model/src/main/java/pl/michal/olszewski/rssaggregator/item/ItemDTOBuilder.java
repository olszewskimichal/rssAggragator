package pl.michal.olszewski.rssaggregator.item;

import java.time.Instant;

public class ItemDTOBuilder {

  private String title;
  private String description;
  private String link;
  private Instant date;
  private String author;
  private String blogId;
  private String imageURL;


  public ItemDTOBuilder from(ItemDTO itemDTO) {
    return new ItemDTOBuilder()
        .title(itemDTO.getTitle())
        .description(itemDTO.getDescription())
        .link(itemDTO.getLink())
        .date(itemDTO.getDate())
        .author(itemDTO.getAuthor())
        .blogId(itemDTO.getBlogId());
  }

  public ItemDTOBuilder title(String title) {
    this.title = title;
    return this;
  }

  public ItemDTOBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ItemDTOBuilder link(String link) {
    this.link = link;
    return this;
  }

  public ItemDTOBuilder date(Instant date) {
    this.date = date;
    return this;
  }

  public ItemDTOBuilder author(String author) {
    this.author = author;
    return this;
  }

  public ItemDTOBuilder blogId(String blogId) {
    this.blogId = blogId;
    return this;
  }

  public ItemDTOBuilder imageURL(String imageURL) {
    this.imageURL = imageURL;
    return this;
  }

  public ItemDTO build() {
    return new ItemDTO(title, description, link, date, author, blogId, imageURL);
  }
}