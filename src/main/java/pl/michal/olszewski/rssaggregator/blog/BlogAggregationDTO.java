package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
class BlogAggregationDTO {

  private String id;
  private String link;
  private String description;
  private String name;
  private String feedURL;
  private Instant publishedDate;
  private Long blogItemsCount;

}
