package pl.michal.olszewski.rssaggregator.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ItemRepository extends ReactiveMongoRepository<Item, String> {
    Flux<Item> findAllBy(Pageable pageable);


}
