package pl.michal.olszewski.rssaggregator.search.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

class ItemSearchResult {

  private final String title;
  private final String description;
  private final String link;
  private final String score;

  @JsonCreator
  ItemSearchResult(
      @JsonProperty("title") String title,
      @JsonProperty("description") String description,
      @JsonProperty("link") String link,
      @JsonProperty("score") String score
  ) {
    this.title = title;
    this.description = description;
    this.link = link;
    this.score = score;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getLink() {
    return link;
  }

  public String getScore() {
    return score;
  }

  @Override
  public final int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public final boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }
}
