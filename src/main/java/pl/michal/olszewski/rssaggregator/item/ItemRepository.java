package pl.michal.olszewski.rssaggregator.item;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface ItemRepository extends JpaRepository<Item, Long> {

  @Query(value = "SELECT TOP(?1) * FROM Item v order by v.date desc", nativeQuery = true)
  List<Item> findAllByOrderByDateDesc(int limit);

}
