package pl.michal.olszewski.rssaggregator.events;

import org.springframework.data.annotation.Id;

abstract class EventBase {

  @Id
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
