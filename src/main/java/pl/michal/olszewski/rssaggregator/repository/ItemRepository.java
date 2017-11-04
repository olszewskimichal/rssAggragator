package pl.michal.olszewski.rssaggregator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  
  @Query(value = "SELECT v FROM Item v order by v.date desc NULLS LAST", countQuery = "select count(v) from Item v")
  Page<Item> findAllByOrderByDateDesc(Pageable pageable);

}
