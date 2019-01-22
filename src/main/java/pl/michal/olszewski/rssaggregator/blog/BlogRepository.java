package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
@Transactional
public interface BlogRepository extends ReactiveMongoRepository<Blog, String> {

    @Query("{ 'feedURL' : ?0 }")
    Optional<Blog> findByFeedURL(String url);

    @Query("{ 'name' : ?0 }")
    Optional<Blog> findByName(String name);

    Mono<Blog> findById(String id);

    @Query("{ 'active' : true}")
    Flux<Blog> findAll();
}
