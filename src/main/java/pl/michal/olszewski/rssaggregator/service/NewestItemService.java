package pl.michal.olszewski.rssaggregator.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.repository.ItemRepository;

@Service
public class NewestItemService {

  private final ItemRepository itemRepository;

  public NewestItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public List<ItemDTO> getNewestItems(int size) {
    return itemRepository.findAllByOrderByDateDesc(new PageRequest(0, size)).getContent().stream().map(v -> new ItemDTO(v.getTitle(), v.getDescription(), v.getLink(), v.getDate(), v.getAuthor()))
        .collect(
            Collectors.toList());
  }
}
