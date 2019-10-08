package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;

public class RssInfo {

  private final String feedURL;
  private final String blogURL;
  private final String blogId;
  private final Instant lastUpdateDate;

  RssInfo(String feedURL, String blogURL, String blogId, Instant lastUpdateDate) {
    this.feedURL = feedURL;
    this.blogURL = blogURL;
    this.blogId = blogId;
    this.lastUpdateDate = lastUpdateDate == null ? Instant.MIN : lastUpdateDate;
  }

  public String getFeedURL() {
    return feedURL;
  }

  public String getBlogURL() {
    return blogURL;
  }

  public String getBlogId() {
    return blogId;
  }

  public Instant getLastUpdateDate() {
    return lastUpdateDate;
  }
}
