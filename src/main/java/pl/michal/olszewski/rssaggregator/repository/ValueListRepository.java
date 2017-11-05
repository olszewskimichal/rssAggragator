package pl.michal.olszewski.rssaggregator.repository;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

import java.util.List;
import java.util.stream.Stream;
import javax.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.entity.ValueList;

@Repository
public interface ValueListRepository extends JpaRepository<ValueList, Long> {

  @Query(value = "SELECT b FROM ValueList b")
  @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + 100))
  Stream<ValueList> findStreamAll();

  @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + 100))
  List<ValueList> findAll();

  @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + 100))
  @Query(value = "SELECT b FROM ValueList b", countQuery = "select count(b) from ValueList b")
  Stream<ValueList> findStreamAll(Pageable pageable);

  @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + 100))
  @Query(value = "SELECT b FROM ValueList b where b.id>?1 and b.id<=?2")
  Stream<ValueList> findStreamAll(Long from,Long to);

}
