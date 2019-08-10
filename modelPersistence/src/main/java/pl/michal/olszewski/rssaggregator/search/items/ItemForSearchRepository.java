package pl.michal.olszewski.rssaggregator.search.items;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ItemForSearchRepository extends MongoRepository<ItemForSearch, String> {

}
