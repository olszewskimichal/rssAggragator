package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

final class ReadItemDTO {

  private final String itemId;
  private final boolean read;

  @Builder
  @JsonCreator
  ReadItemDTO(@JsonProperty("itemId") String itemId, @JsonProperty("read") boolean read) {
    this.itemId = itemId;
    this.read = read;
  }

  public String getItemId() {
    return itemId;
  }

  public boolean isRead() {
    return read;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }
}
