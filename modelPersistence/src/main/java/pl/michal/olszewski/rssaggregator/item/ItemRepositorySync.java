package pl.michal.olszewski.rssaggregator.item;

import java.util.stream.Stream;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ItemRepositorySync extends MongoRepository<Item, String> {

  String DATE = "date";

  Stream<Item> findAllBy(Pageable pageable);

  default Stream<Item> findAllOrderByPublishedDate(Integer limit, Integer page) {
    return findAllBy(PageRequest.of(page, limit, new Sort(Sort.Direction.DESC, DATE)));
  }
}

