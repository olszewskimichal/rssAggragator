package pl.michal.olszewski.rssaggregator.search;

public class NewItemForSearchEventBuilder {

  private String linkUrl;
  private String itemTitle;
  private String itemDescription;

  public NewItemForSearchEventBuilder linkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
    return this;
  }

  public NewItemForSearchEventBuilder itemTitle(String itemTitle) {
    this.itemTitle = itemTitle;
    return this;
  }

  public NewItemForSearchEventBuilder itemDescription(String itemDescription) {
    this.itemDescription = itemDescription;
    return this;
  }

  public NewItemForSearchEvent build() {
    return new NewItemForSearchEvent(linkUrl, itemTitle, itemDescription);
  }
}