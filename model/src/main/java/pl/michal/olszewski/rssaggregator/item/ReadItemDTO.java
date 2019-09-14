package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
final class ReadItemDTO {

  private final String itemId;
  private final boolean read;

  @Builder
  @JsonCreator
  ReadItemDTO(@JsonProperty("itemId") String itemId, @JsonProperty("read") boolean read) {
    this.itemId = itemId;
    this.read = read;
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemId, read);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReadItemDTO that = (ReadItemDTO) o;
    return read == that.read &&
        Objects.equals(itemId, that.itemId);
  }
}
