package pl.michal.olszewski.rssaggregator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.michal.olszewski.rssaggregator.entity.Blog;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Optional<Blog> findByBlogURL(String url);

    Optional<Blog> findById(Long id);
}
