package pl.michal.olszewski.rssaggregator.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
final class BlogItemDTO {

  private final String id;
  private final String title;
  private final String link;
  private final Instant date;
  private final String author;

  public BlogItemDTO(
      @JsonProperty("id") String id,
      @JsonProperty("title") String title,
      @JsonProperty("link") String link,
      @JsonProperty("date") Instant date,
      @JsonProperty("author") String author
  ) {
    this.id = id;
    this.title = title;
    this.link = link;
    this.date = date;
    this.author = author;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getLink() {
    return link;
  }

  public Instant getDate() {
    return date;
  }

  public String getAuthor() {
    return author;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, link, date, author);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BlogItemDTO)) {
      return false;
    }
    BlogItemDTO that = (BlogItemDTO) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(title, that.title) &&
        Objects.equals(link, that.link) &&
        Objects.equals(date, that.date) &&
        Objects.equals(author, that.author);
  }

  @Override
  public String toString() {
    return "BlogItemDTO{" +
        "id='" + id + '\'' +
        ", title='" + title + '\'' +
        ", link='" + link + '\'' +
        ", date=" + date +
        ", author='" + author + '\'' +
        '}';
  }
}
