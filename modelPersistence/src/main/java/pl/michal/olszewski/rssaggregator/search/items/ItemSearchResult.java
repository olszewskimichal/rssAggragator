package pl.michal.olszewski.rssaggregator.search.items;

import lombok.Data;

@Data
class ItemSearchResult {

  private String id;
  private String title;
  private String description;
  private String link;
}
