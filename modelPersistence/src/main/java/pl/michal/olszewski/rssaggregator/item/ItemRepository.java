package pl.michal.olszewski.rssaggregator.item;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ItemRepository extends MongoRepository<Item, String> {

  String DATE = "date";
  String CREATED_AT = "createdAt";

  List<Item> findAllBy(Pageable pageable);

  List<Item> findAllByBlogId(String blogId);

  default List<Item> findAllOrderByPublishedDate(Integer limit, Integer page) {
    return findAllBy(PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, DATE)));
  }

  default List<Item> findAllOrderByCreatedAt(Integer limit, Integer page) {
    return findAllBy(PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, CREATED_AT)));
  }


}
