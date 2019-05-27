package pl.michal.olszewski.rssaggregator.events;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EventRepository extends ReactiveMongoRepository<EventBase, String> {

}
