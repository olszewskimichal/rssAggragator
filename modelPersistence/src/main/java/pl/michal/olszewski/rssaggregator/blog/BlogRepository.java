package pl.michal.olszewski.rssaggregator.blog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends MongoRepository<Blog, String>, CustomBlogRepository {

  @Query("{ 'feedURL' : ?0 }")
  Optional<Blog> findByFeedURL(String url);

  @Query("{ 'name' : ?0 }")
  Optional<Blog> findByName(String name);

  @Query("{ 'active' : true}")
  List<Blog> findAll();
}
