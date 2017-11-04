package pl.michal.olszewski.rssaggregator.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.repository.ItemRepository;

@Service
public class NewestItemService {

  private final ItemRepository itemRepository;

  public NewestItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public List<Item> getNewestItems(int size) {
    return itemRepository.findAllByOrderByDateDesc(new PageRequest(0, size)).getContent();
  }
}
