package pl.michal.olszewski.rssaggregator.blog.activity;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeActivityBlogEventRepository extends ReactiveMongoRepository<ChangeActivityBlogEvent, String> {

}
