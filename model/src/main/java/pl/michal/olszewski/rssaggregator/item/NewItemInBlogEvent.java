package pl.michal.olszewski.rssaggregator.item;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class NewItemInBlogEvent implements Serializable {

  private final ItemDTO itemDTO;
  private final String blogId;

  public NewItemInBlogEvent(ItemDTO itemDTO, String blogId) {
    this.itemDTO = itemDTO;
    this.blogId = blogId;
  }

  public ItemDTO getItemDTO() {
    return itemDTO;
  }

  public String getBlogId() {
    return blogId;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
