package pl.michal.olszewski.rssaggregator.blog;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface BlogSyncRepository extends MongoRepository<Blog, String> {

  @Query("{ 'active' : true}")
  List<Blog> findAll();
}
