package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.util.List;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;

public class UpdateBlogWithItemsDTOBuilder {

  private String link;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;
  private List<ItemDTO> itemsList;

  public UpdateBlogWithItemsDTOBuilder link(String link) {
    this.link = link;
    return this;
  }

  public UpdateBlogWithItemsDTOBuilder description(String description) {
    this.description = description;
    return this;
  }

  public UpdateBlogWithItemsDTOBuilder name(String name) {
    this.name = name;
    return this;
  }

  public UpdateBlogWithItemsDTOBuilder feedURL(String feedURL) {
    this.feedURL = feedURL;
    return this;
  }

  public UpdateBlogWithItemsDTOBuilder publishedDate(Instant publishedDate) {
    this.publishedDate = publishedDate;
    return this;
  }

  public UpdateBlogWithItemsDTOBuilder itemsList(List<ItemDTO> itemsList) {
    this.itemsList = itemsList;
    return this;
  }

  public UpdateBlogWithItemsDTO build() {
    return new UpdateBlogWithItemsDTO(link, description, name, feedURL, publishedDate, itemsList);
  }
}