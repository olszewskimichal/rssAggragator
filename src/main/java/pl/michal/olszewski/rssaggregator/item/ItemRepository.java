package pl.michal.olszewski.rssaggregator.item;

import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.item.Item;

@Repository
interface ItemRepository extends JpaRepository<Item, Long> {

  @Query(value = "SELECT TOP(?1) * FROM Item v order by v.date desc", nativeQuery = true)
  Stream<Item> findAllByOrderByDateDesc(int limit);

}
