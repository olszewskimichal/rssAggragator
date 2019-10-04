package pl.michal.olszewski.rssaggregator.item;

class ItemToDtoMapper {

  static ItemDTO mapItemToItemDTO(Item item) {
    return new ItemDTO(
        item.getTitle(),
        item.getDescription(),
        item.getLink(),
        item.getDate(),
        item.getAuthor(),
        item.getBlogId()
    );
  }

  static BlogItemDTO mapToBlogItemDTO(Item item) {
    return new BlogItemDTO(
        item.getId(),
        item.getTitle(),
        item.getLink(),
        item.getDate(),
        item.getAuthor()
    );
  }

}
