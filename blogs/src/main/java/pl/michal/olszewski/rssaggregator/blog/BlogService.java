package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.NewItemInBlogEvent;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class BlogService {

  private final BlogFinder blogFinder;
  private final BlogUpdater blogUpdater;
  private final Cache<String, BlogDTO> blogCache;
  private final Cache<String, ItemDTO> itemCache;
  private final NewItemInBlogEventProducer producer;
  private final NewItemForSearchEventProducer itemForSearchEventProducer;

  public BlogService(
      BlogFinder blogFinder,
      BlogUpdater blogUpdater,
      @Qualifier("blogCache") Cache<String, BlogDTO> blogCache,
      @Qualifier("itemCache") Cache<String, ItemDTO> itemCache,
      NewItemInBlogEventProducer producer,
      NewItemForSearchEventProducer itemForSearchEventProducer) {
    this.blogFinder = blogFinder;
    this.blogUpdater = blogUpdater;
    this.blogCache = blogCache;
    this.itemCache = itemCache;
    this.producer = producer;
    this.itemForSearchEventProducer = itemForSearchEventProducer;
  }

  public Mono<Blog> updateBlog(Blog blogFromDb, UpdateBlogWithItemsDTO blogInfoFromRSS) {
    log.debug("aktualizuje bloga {}", blogFromDb.getName());
    blogInfoFromRSS.getItemsList()
        .forEach(item -> addItemToBlog(blogFromDb, item));
    return blogUpdater.updateBlogFromDTO(blogFromDb, blogInfoFromRSS.toUpdateBlogDto())
        .doOnNext(this::putToCache);
  }

  public Flux<BlogDTO> getAllBlogDTOs() {
    log.debug("pobieram wszystkie blogi w postaci DTO");
    var dtoFlux = Flux.fromIterable(blogCache.asMap().values())
        .switchIfEmpty(Flux.defer(() -> blogFinder.findAll()
            .map(BlogToDtoMapper::mapToBlogDto)
            .doOnNext(blog -> blogCache.put(blog.getId(), blog)))
            .cache());
    return dtoFlux
        .doOnEach(blogDTO -> log.trace("getAllBlogDTOs {}", blogDTO));
  }

  Mono<BlogDTO> getBlogOrCreate(CreateBlogDTO blogDTO) {
    log.debug("Tworzenie nowego bloga {}", blogDTO.getFeedURL());
    return blogFinder.findByFeedURL(blogDTO.getFeedURL())
        .switchIfEmpty(Mono.defer(() -> createBlog(blogDTO)))
        .map(blog -> new BlogDTO(
            blog.getId(),
            blog.getBlogURL(),
            blog.getDescription(),
            blog.getName(),
            blog.getFeedURL(),
            blog.getPublishedDate()
        ));
  }

  Mono<Void> deleteBlog(String id) {
    log.debug("Usuwam bloga o id {}", id);
    return blogFinder.getBlogWithCount(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(blog -> {
          if (blog.getBlogItemsCount() == 0) {
            return blogUpdater.deleteBlogById(blog.getBlogId())
                .doOnSuccess(v -> blogCache.invalidate(id));
          }
          return blogFinder.findById(id)
              .flatMap(blogById -> {
                blogById.deactivate();
                return Mono.empty();
              });
        });
  }

  Mono<BlogDTO> getBlogDTOById(String id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    return Mono.justOrEmpty(blogCache.getIfPresent(id))
        .switchIfEmpty(Mono.defer(() -> blogFinder.findById(id).cache()
            .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
            .map(BlogToDtoMapper::mapToBlogDto)
            .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id))
            .doOnSuccess(result -> blogCache.put(result.getId(), result))));
  }

  void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
    blogCache.invalidateAll();
  }

  Mono<BlogDTO> updateBlog(UpdateBlogDTO blogDTO) {
    log.debug("Aktualizacja bloga {}", blogDTO.getName());
    return getBlogByFeedUrl(blogDTO.getFeedURL())
        .flatMap(blog -> updateBlog(blog, blogDTO))
        .map(blog -> new BlogDTO(
            blog.getId(),
            blog.getBlogURL(),
            blog.getDescription(),
            blog.getName(),
            blog.getFeedURL(),
            blog.getPublishedDate()
        ))
        .doOnSuccess(updatedBlog -> blogCache.invalidate(updatedBlog.getId()));
  }

  private void addItemToBlog(Blog blog, ItemDTO item) {
    if (itemCache.getIfPresent(item.getLink()) == null) {
      itemCache.put(item.getLink(), item);
      producer.writeEventToQueue(new NewItemInBlogEvent(Instant.now(), item, blog.getId()));
      itemForSearchEventProducer.writeEventToQueue(new NewItemForSearchEvent(Instant.now(), item.getLink(), item.getTitle(), item.getDescription()));
    }
  }

  private void putToCache(Blog updatedBlog) {
    blogCache.put(
        updatedBlog.getId(),
        new BlogDTO(
            updatedBlog.getId(),
            updatedBlog.getBlogURL(),
            updatedBlog.getDescription(),
            updatedBlog.getName(),
            updatedBlog.getFeedURL(),
            updatedBlog.getPublishedDate()
        )
    );
  }

  private Mono<Blog> createBlog(CreateBlogDTO blogDTO) {
    log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());
    return blogUpdater.createNewBlog(blogDTO)
        .doOnNext(this::putToCache);
  }

  private Mono<Blog> getBlogByFeedUrl(String feedUrl) {
    log.debug("getBlogByFeedUrl feedUrl {}", feedUrl);
    return blogFinder.findByFeedURL(feedUrl)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(feedUrl)));
  }

  private Mono<Blog> updateBlog(Blog blogFromDb, UpdateBlogDTO blogInfoFromRSS) {
    log.debug("aktualizuje bloga {}", blogFromDb.getName());
    return blogUpdater.updateBlogFromDTO(blogFromDb, blogInfoFromRSS)
        .doOnNext(this::putToCache);
  }
}
