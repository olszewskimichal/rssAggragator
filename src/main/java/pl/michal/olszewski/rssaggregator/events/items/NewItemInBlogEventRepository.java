package pl.michal.olszewski.rssaggregator.events.items;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface NewItemInBlogEventRepository extends MongoRepository<NewItemInBlogEvent, String> {

}
