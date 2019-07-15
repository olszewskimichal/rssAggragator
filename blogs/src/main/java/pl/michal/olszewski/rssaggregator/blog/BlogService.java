package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.blog.newitem.NewItemInBlogEventProducer;
import pl.michal.olszewski.rssaggregator.item.Item;
import pl.michal.olszewski.rssaggregator.newitem.NewItemInBlogEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@Slf4j
public
class BlogService {

  private final BlogReactiveRepository blogRepository;
  private final MongoTemplate mongoTemplate;
  private final Cache<String, BlogAggregationDTO> cache;
  private final NewItemInBlogEventProducer producer;

  public BlogService(
      BlogReactiveRepository blogRepository,
      MongoTemplate mongoTemplate,
      @Qualifier("blogCache") Cache<String, BlogAggregationDTO> cache,
      NewItemInBlogEventProducer producer
  ) {
    this.blogRepository = blogRepository;
    this.mongoTemplate = mongoTemplate;
    this.cache = cache;
    this.producer = producer;
  }

  Mono<BlogDTO> getBlogOrCreate(BlogDTO blogDTO) {
    log.debug("Tworzenie nowego bloga {}", blogDTO.getFeedURL());
    return blogRepository.findByFeedURL(blogDTO.getFeedURL())
        .switchIfEmpty(Mono.defer(() -> createBlog(blogDTO)))
        .map(BlogDTO::new);
  }

  private Mono<Blog> createBlog(BlogDTO blogDTO) {
    log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());
    var blog = new Blog(blogDTO);
    blogDTO.getItemsList().stream()
        .map(Item::new)
        .forEach(item -> addItemToBlog(blog, item));
    return blogRepository.save(blog)
        .doOnNext(createdBlog -> cache.put(createdBlog.getId(), new BlogAggregationDTO(createdBlog)));
  }

  private Mono<Blog> getBlogByFeedUrl(String feedUrl) {
    log.debug("getBlogByFeedUrl feedUrl {}", feedUrl);
    return blogRepository.findByFeedURL(feedUrl)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(feedUrl)));
  }

  @Transactional
  public Mono<Blog> updateBlog(Blog blogFromDb, BlogDTO blogInfoFromRSS) {
    return Mono.just(blogFromDb).
        flatMap(
            blog -> {
              log.debug("aktualizuje bloga {}", blog.getName());
              Set<String> linkSet = blog.getItems().stream()
                  .parallel()
                  .map(Item::getLink)
                  .collect(Collectors.toSet());
              blogInfoFromRSS.getItemsList().stream()
                  .map(Item::new)
                  .filter(item -> !linkSet.contains(item.getLink()))
                  .forEach(item -> addItemToBlog(blog, item));
              blog.updateFromDto(blogInfoFromRSS);
              return blogRepository.save(blog)
                  .doOnNext(updatedBlog -> cache.put(updatedBlog.getId(), new BlogAggregationDTO(updatedBlog)));
            }
        );
  }

  Mono<Void> deleteBlog(String id) {
    log.debug("Usuwam bloga o id {}", id);
    return blogRepository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(blog -> {
          if (blog.getItems().isEmpty()) {
            return blogRepository.delete(blog)
                .doOnSuccess(v -> cache.invalidate(id));
          }
          blog.deactivate();
          return Mono.empty();
        });
  }

  @Transactional(readOnly = true)
  public Mono<BlogAggregationDTO> getBlogDTOById(String id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    return Mono.justOrEmpty(cache.getIfPresent(id))
        .switchIfEmpty(Mono.defer(() -> blogRepository.findById(id).cache()
            .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
            .map(BlogAggregationDTO::new)
            .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id))
            .doOnSuccess(v -> cache.put(id, v))));
  }

  @Transactional(readOnly = true)
  public Flux<BlogAggregationDTO> getAllBlogDTOs() {
    log.debug("pobieram wszystkie blogi w postaci DTO");
    var dtoFlux = Flux.fromIterable(cache.asMap().values())
        .switchIfEmpty(Flux.defer(() -> blogRepository.getBlogsWithCount()
            .doOnNext(blog -> cache.put(blog.getBlogId(), blog)))
            .cache());
    return dtoFlux
        .doOnEach(blogDTO -> log.trace("getAllBlogDTOs {}", blogDTO));
  }

  void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
    cache.invalidateAll();
  }

  Mono<BlogDTO> updateBlog(BlogDTO blogDTO) {
    log.debug("Aktualizacja bloga {}", blogDTO.getLink());
    return getBlogByFeedUrl(blogDTO.getFeedURL())
        .flatMap(blog -> updateBlog(blog, blogDTO))
        .map(BlogDTO::new);
  }

  private void addItemToBlog(Blog blog, Item item) {
    blog.addItem(item, mongoTemplate);
    producer.writeEventToQueue(new NewItemInBlogEvent(Instant.now(), item.getLink(), item.getTitle(), blog.getId()));
  }
}
