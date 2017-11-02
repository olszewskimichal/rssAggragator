package pl.michal.olszewski.rssaggregator.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ItemDTO {

  private final String title;
  private final String description;
  private final String link;
  private final Instant date;
  private final String author;
}
