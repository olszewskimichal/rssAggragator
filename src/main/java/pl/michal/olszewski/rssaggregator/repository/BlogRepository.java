package pl.michal.olszewski.rssaggregator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.entity.Blog;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    @Query("select b from Blog b left join fetch b.items where b.blogURL=?1")
    Optional<Blog> findByBlogURL(String url);

    @Query("select b from Blog b left join fetch b.items where b.id=?1")
    Optional<Blog> findById(Long id);
}
