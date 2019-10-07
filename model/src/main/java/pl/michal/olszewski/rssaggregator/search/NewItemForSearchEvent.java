package pl.michal.olszewski.rssaggregator.search;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NewItemForSearchEvent implements Serializable {

  private final String linkUrl;
  private final String itemTitle;
  private final String itemDescription;

  public NewItemForSearchEvent(String linkUrl, String itemTitle, String itemDescription) {
    this.linkUrl = linkUrl;
    this.itemTitle = itemTitle;
    this.itemDescription = itemDescription;
  }

  public String getLinkUrl() {
    return linkUrl;
  }

  public String getItemTitle() {
    return itemTitle;
  }

  public String getItemDescription() {
    return itemDescription;
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
