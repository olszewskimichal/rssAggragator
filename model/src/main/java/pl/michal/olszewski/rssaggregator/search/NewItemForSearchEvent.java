package pl.michal.olszewski.rssaggregator.search;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
public class NewItemForSearchEvent implements Serializable {

  private final Instant occurredAt;
  private final String linkUrl;
  private final String itemTitle;
  private final String itemDescription;
}
