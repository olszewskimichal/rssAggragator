package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.newitem.NewItemInBlogEventProducer;
import pl.michal.olszewski.rssaggregator.blog.search.NewItemForSearchEventProducer;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.NewItemInBlogEvent;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public
class BlogService {

  private final BlogReactiveRepository blogRepository;
  private final Cache<String, BlogAggregationDTO> cache;
  private final Cache<String, ItemDTO> itemCache;
  private final NewItemInBlogEventProducer producer;
  private final NewItemForSearchEventProducer itemForSearchEventProducer;

  public BlogService(
      BlogReactiveRepository blogRepository,
      @Qualifier("blogCache") Cache<String, BlogAggregationDTO> cache,
      @Qualifier("itemCache") Cache<String, ItemDTO> itemCache,
      NewItemInBlogEventProducer producer,
      NewItemForSearchEventProducer itemForSearchEventProducer) {
    this.blogRepository = blogRepository;
    this.cache = cache;
    this.itemCache = itemCache;
    this.producer = producer;
    this.itemForSearchEventProducer = itemForSearchEventProducer;
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
    blogDTO.getItemsList()
        .forEach(item -> addItemToBlog(blog, item));
    return blogRepository.save(blog)
        .doOnNext(createdBlog -> cache.put(createdBlog.getId(), new BlogAggregationDTO(createdBlog.getId(), blogDTO)));
  }

  private Mono<Blog> getBlogByFeedUrl(String feedUrl) {
    log.debug("getBlogByFeedUrl feedUrl {}", feedUrl);
    return blogRepository.findByFeedURL(feedUrl)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(feedUrl)));
  }

  public Mono<Blog> updateBlog(Blog blogFromDb, BlogDTO blogInfoFromRSS) {
    return Mono.just(blogFromDb).
        flatMap(
            blog -> {
              log.debug("aktualizuje bloga {}", blog.getName());
              blogInfoFromRSS.getItemsList()
                  .forEach(item -> addItemToBlog(blog, item));
              blog.updateFromDto(blogInfoFromRSS);
              return blogRepository.save(blog)
                  .doOnNext(updatedBlog -> cache.put(updatedBlog.getId(), new BlogAggregationDTO(blog.getId(), new BlogDTO(updatedBlog))));
            }
        );
  }

  Mono<Void> deleteBlog(String id) {
    log.debug("Usuwam bloga o id {}", id);
    return blogRepository.getBlogWithCount(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(blog -> {
          if (blog.getBlogItemsCount() == 0) {
            return blogRepository.deleteById(blog.getBlogId())
                .doOnSuccess(v -> cache.invalidate(id));
          }
          return blogRepository.findById(id)
              .flatMap(blogById -> {
                blogById.deactivate();
                return Mono.empty();
              });
        });
  }

  public Mono<BlogAggregationDTO> getBlogDTOById(String id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    return Mono.justOrEmpty(cache.getIfPresent(id))
        .switchIfEmpty(Mono.defer(() -> blogRepository.findById(id).cache()
            .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
            .map(blog -> new BlogAggregationDTO(id, new BlogDTO(blog)))
            .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id))
            .doOnSuccess(v -> cache.put(id, v))));
  }

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

  private void addItemToBlog(Blog blog, ItemDTO item) {
    if (itemCache.getIfPresent(item.getLink()) == null) {
      itemCache.put(item.getLink(), item);
      producer.writeEventToQueue(new NewItemInBlogEvent(Instant.now(), item, blog.getId()));
      itemForSearchEventProducer.writeEventToQueue(new NewItemForSearchEvent(Instant.now(), item.getLink(), item.getTitle(), item.getDescription()));
    }
  }
}
