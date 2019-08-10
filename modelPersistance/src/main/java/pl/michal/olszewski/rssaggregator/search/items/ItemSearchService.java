package pl.michal.olszewski.rssaggregator.search.items;

import org.springframework.stereotype.Service;

@Service
class ItemSearchService {

  private final ItemForSearchRepository itemForSearchRepository;

  ItemSearchService(ItemForSearchRepository itemForSearchRepository) {
    this.itemForSearchRepository = itemForSearchRepository;
  }

  void saveItemForSearch(ItemForSearch itemForSearch) {
    itemForSearchRepository.save(itemForSearch);
  }
}
