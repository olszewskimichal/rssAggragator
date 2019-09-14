package pl.michal.olszewski.rssaggregator.item;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface NewItemInBlogEventRepository extends MongoRepository<NewItemInBlogEvent, String> {

}
