package pl.michal.olszewski.rssaggregator.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ItemRepository extends ReactiveMongoRepository<Item, String> {
    String DATE = "date";
    Flux<Item> findAllBy(Pageable pageable);

    default Flux<Item> findAllNew(Integer limit){
        return findAllBy(PageRequest.of(0, limit, new Sort(Sort.Direction.DESC, DATE)));
    }



}
