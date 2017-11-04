package pl.michal.olszewski.rssaggregator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

  Page<Item> findAllOrderByDateDesc(Pageable pageable);

}
