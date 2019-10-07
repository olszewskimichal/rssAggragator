package pl.michal.olszewski.rssaggregator.search.items;

public class ItemForSearchBuilder {

  private String title;
  private String description;
  private String link;
  private float score;

  public ItemForSearchBuilder title(String title) {
    this.title = title;
    return this;
  }

  public ItemForSearchBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ItemForSearchBuilder link(String link) {
    this.link = link;
    return this;
  }

  public ItemForSearchBuilder score(float score) {
    this.score = score;
    return this;
  }

  public ItemForSearch build() {
    return new ItemForSearch(title, description, link, score);
  }
}