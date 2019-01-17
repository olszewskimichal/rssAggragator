package pl.michal.olszewski.rssaggregator.blog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

  @Query("select b from Blog b left join fetch b.items where b.feedURL=?1")
  Optional<Blog> findByFeedURL(String url);

  @Query("select b from Blog b left join fetch b.items where b.name=?1")
  Optional<Blog> findByName(String name);

  @Query("select b from Blog b left join fetch b.items where b.id=?1")
  Optional<Blog> findById(Long id);

  @Query(value = "SELECT distinct b FROM Blog b LEFT JOIN FETCH b.items where b.active=1")
  List<Blog> findAll();

}
