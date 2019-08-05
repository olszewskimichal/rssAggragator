package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BlogReactiveRepository extends ReactiveMongoRepository<Blog, String>, ReactiveAggregationBlogRepository {

  @Query("{ 'feedURL' : ?0 }")
  Mono<Blog> findByFeedURL(String url);

  @Query("{ 'name' : ?0 }")
  Mono<Blog> findByName(String name);

  Mono<Blog> findById(String id);

  @Query("{ 'active' : true}")
  Flux<Blog> findAll();
}
