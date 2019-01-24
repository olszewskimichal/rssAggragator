package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Repository
@Transactional
public interface BlogReactiveRepository extends ReactiveMongoRepository<Blog, String> {

  @Query("{ 'active' : true}")
  Flux<Blog> findAll();
}
