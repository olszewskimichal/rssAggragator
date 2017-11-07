package pl.michal.olszewski.rssaggregator.repository;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.entity.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

  @Cacheable("blogsURL")
  @Query("select b from Blog b left join fetch b.items where b.blogURL=?1")
  Optional<Blog> findByBlogURL(String url);

  @Query("select b from Blog b left join fetch b.items where b.id=?1")
  Optional<Blog> findById(Long id);

  @Query(value = "SELECT b FROM Blog b LEFT JOIN FETCH b.items")
  Stream<Blog> findStreamAll();

}
