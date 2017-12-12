package pl.michal.olszewski.rssaggregator.repository;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.entity.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

  @Query("select b from Blog b left join fetch b.items where b.feedURL=?1")
  Optional<Blog> findByFeedURL(String url);

  @Query("select b from Blog b left join fetch b.items where b.name=?1")
  Optional<Blog> findByName(String name);

  @Query("select b from Blog b left join fetch b.items where b.id=?1")
  Optional<Blog> findById(Long id);

  @Query(value = "SELECT b FROM Blog b LEFT JOIN FETCH b.items where b.active=1")
  Stream<Blog> findStreamAll();

}
