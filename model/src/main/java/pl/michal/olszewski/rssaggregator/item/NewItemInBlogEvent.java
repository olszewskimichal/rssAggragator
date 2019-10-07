package pl.michal.olszewski.rssaggregator.item;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class NewItemInBlogEvent implements Serializable {

  private final ItemDTO itemDTO;

  public NewItemInBlogEvent(ItemDTO itemDTO) {
    this.itemDTO = itemDTO;
  }

  public ItemDTO getItemDTO() {
    return itemDTO;
  }

  @Override
  public final int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public final boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
