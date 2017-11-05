package pl.michal.olszewski.rssaggregator.repository;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.entity.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

  @Query("select b from Blog b left join fetch b.items where b.blogURL=?1")
  Optional<Blog> findByBlogURL(String url);

  @Query("select b from Blog b left join fetch b.items where b.id=?1")
  Optional<Blog> findById(Long id);

  @Query(value = "SELECT b FROM Blog b LEFT JOIN FETCH b.items",
      countQuery = "select count(b) from Blog b")
  Page<Blog> findAll(Pageable pageable);
  
}
