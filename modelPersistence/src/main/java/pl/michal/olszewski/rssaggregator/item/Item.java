package pl.michal.olszewski.rssaggregator.item;

import java.time.Instant;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndex(def = "{'link':1, 'blogId':1}", name = "uniqueBlogItemIndex", unique = true)
final class Item {

  @Id
  private String id;
  private String title;
  private String description;
  private String link;
  private Instant date;
  private String author;
  private boolean read;

  @CreatedDate
  private Instant createdAt;
  private String blogId;
  private String imageURL;

  public Item(ItemDTO itemDTO) {
    this.title = itemDTO.getTitle();
    this.description = itemDTO.getDescription();
    this.link = itemDTO.getLink();
    this.date = itemDTO.getDate();
    this.author = itemDTO.getAuthor();
    this.read = false;
    this.blogId = itemDTO.getBlogId();
    this.imageURL = itemDTO.getImageURL();
  }

  public Item() {
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getLink() {
    return link;
  }

  public boolean isRead() {
    return read;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public String getBlogId() {
    return blogId;
  }


  Instant getDate() {
    return date;
  }

  String getAuthor() {
    return author;
  }

  public String getImageURL() {
    return imageURL;
  }

  Item markAsRead() {
    this.read = true;
    return this;
  }


  Item markAsUnread() {
    this.read = false;
    return this;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  void updateLink(String newLink) {
    this.link = newLink;
  }

  @Override
  public final int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, "id", "createdAt", "read");
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o, "id", "createdAt", "read");
  }

  void updateImageUrl(String imageURL) {
    this.imageURL = imageURL;
  }

}
