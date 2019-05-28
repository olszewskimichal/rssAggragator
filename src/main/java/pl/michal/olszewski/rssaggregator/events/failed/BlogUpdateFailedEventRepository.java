package pl.michal.olszewski.rssaggregator.events.failed;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface BlogUpdateFailedEventRepository extends MongoRepository<BlogUpdateFailedEvent, String> {

}
