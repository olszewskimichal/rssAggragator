package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Singular;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;

public final class UpdateBlogWithItemsDTO {

  private final String link;
  private final String description;
  private final String name;
  private final String feedURL;
  private final Instant publishedDate;
  private final List<ItemDTO> itemsList;

  @Builder
  public UpdateBlogWithItemsDTO(
      String link,
      String description,
      String name,
      String feedURL,
      Instant publishedDate,
      @Singular("item") List<ItemDTO> itemsList
  ) {
    this.link = link;
    this.description = description;
    this.name = name;
    this.feedURL = feedURL;
    this.publishedDate = publishedDate;
    this.itemsList = Optional.ofNullable(itemsList).orElse(List.of());
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public String getFeedURL() {
    return feedURL;
  }

  public Instant getPublishedDate() {
    return publishedDate;
  }

  public void addNewItem(ItemDTO itemDTO) {
    this.itemsList.add(itemDTO);
  }

  @Override
  public int hashCode() {
    return Objects.hash(link, description, name, feedURL, publishedDate, itemsList);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateBlogWithItemsDTO blogDTO = (UpdateBlogWithItemsDTO) o;
    return Objects.equals(link, blogDTO.link) &&
        Objects.equals(description, blogDTO.description) &&
        Objects.equals(name, blogDTO.name) &&
        Objects.equals(feedURL, blogDTO.feedURL) &&
        Objects.equals(publishedDate, blogDTO.publishedDate) &&
        Objects.equals(itemsList, blogDTO.itemsList);
  }

  public UpdateBlogDTO toUpdateBlogDto() {
    return new UpdateBlogDTO(
        link,
        description,
        name,
        feedURL,
        getPublishedDate()
    );
  }

  List<ItemDTO> getItemsList() {
    return Collections.unmodifiableList(itemsList);
  }
}
