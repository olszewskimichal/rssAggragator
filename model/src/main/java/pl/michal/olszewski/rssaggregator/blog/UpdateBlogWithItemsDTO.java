package pl.michal.olszewski.rssaggregator.blog;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;

public final class UpdateBlogWithItemsDTO {

  private final String link;
  private final String description;
  private final String name;
  private final String feedURL;
  private final Instant publishedDate;
  private final List<ItemDTO> itemsList;

  public UpdateBlogWithItemsDTO(
      String link,
      String description,
      String name,
      String feedURL,
      Instant publishedDate,
      List<ItemDTO> itemsList
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

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  public void addNewItem(ItemDTO itemDTO) {
    this.itemsList.add(itemDTO);
  }

  List<ItemDTO> getItemsList() {
    return Collections.unmodifiableList(itemsList);
  }

  public UpdateBlogDTO toUpdateBlogDto() {
    return new UpdateBlogDTOBuilder().link(link).description(description).name(name).feedURL(feedURL).publishedDate(getPublishedDate()).build();
  }
}
