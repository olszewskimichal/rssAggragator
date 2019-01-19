package pl.michal.olszewski.rssaggregator.blog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface BlogRepository extends MongoRepository<Blog, String> {

  @Query("{ 'feedURL' : ?0 }")
  Optional<Blog> findByFeedURL(String url);

  @Query("{ 'name' : ?0 }")
  Optional<Blog> findByName(String name);

  Optional<Blog> findById(String id);

  @Query("{ 'active' : true}")
  List<Blog> findAll();
}
