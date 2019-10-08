package pl.michal.olszewski.rssaggregator.item;

class ItemToDtoMapper {

  private ItemToDtoMapper() {
  }

  static ItemDTO mapItemToItemDTO(Item item) {
    return new ItemDTOBuilder().title(item.getTitle()).description(item.getDescription()).link(item.getLink()).date(item.getDate()).author(item.getAuthor()).blogId(item.getBlogId()).build();
  }

  static BlogItemDTO mapToBlogItemDTO(Item item) {
    return new BlogItemDTO(
        item.getId(),
        item.getTitle(),
        item.getLink(),
        item.getDate(),
        item.getAuthor(),
        item.getImageURL()
    );
  }

}
