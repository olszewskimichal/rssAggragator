package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import java.util.List;
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
  private final Cache<String, BlogAggregationDTO> blogAggregationCache;
  private final Cache<String, ItemDTO> itemCache;
  private final NewItemInBlogEventProducer producer;
  private final NewItemForSearchEventProducer itemForSearchEventProducer;

  public BlogService(
      BlogReactiveRepository blogRepository,
      @Qualifier("blogCache") Cache<String, BlogAggregationDTO> blogAggregationCache,
      @Qualifier("itemCache") Cache<String, ItemDTO> itemCache,
      NewItemInBlogEventProducer producer,
      NewItemForSearchEventProducer itemForSearchEventProducer) {
    this.blogRepository = blogRepository;
    this.blogAggregationCache = blogAggregationCache;
    this.itemCache = itemCache;
    this.producer = producer;
    this.itemForSearchEventProducer = itemForSearchEventProducer;
  }

  Mono<BlogDTO> getBlogOrCreate(BlogDTO blogDTO) {
    log.debug("Tworzenie nowego bloga {}", blogDTO.getFeedURL());
    return blogRepository.findByFeedURL(blogDTO.getFeedURL())
        .switchIfEmpty(Mono.defer(() -> createBlog(blogDTO)))
        .map(blog -> new BlogDTO(
            blog.getBlogURL(),
            blog.getDescription(),
            blog.getName(),
            blog.getFeedURL(),
            blog.getPublishedDate(),
            List.of()
        ));
  }

  private Mono<Blog> createBlog(BlogDTO blogDTO) {
    log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());
    var blog = new Blog(blogDTO);
    blogDTO.getItemsList()
        .forEach(item -> addItemToBlog(blog, item));
    return blogRepository.save(blog)
        .doOnNext(createdBlog -> blogAggregationCache.put(createdBlog.getId(), new BlogAggregationDTO(createdBlog.getId(), blogDTO)));
  }

  private Mono<Blog> getBlogByFeedUrl(String feedUrl) {
    log.debug("getBlogByFeedUrl feedUrl {}", feedUrl);
    return blogRepository.findByFeedURL(feedUrl)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(feedUrl)));
  }

  public Mono<Blog> updateBlog(Blog blogFromDb, BlogDTO blogInfoFromRSS) {
    log.debug("aktualizuje bloga {}", blogFromDb.getName());
    blogInfoFromRSS.getItemsList()
        .forEach(item -> addItemToBlog(blogFromDb, item));
    blogFromDb.updateFromDto(blogInfoFromRSS);
    return blogRepository.save(blogFromDb)
        .doOnNext(this::putToCache);
  }

  private void putToCache(Blog updatedBlog) {
    blogAggregationCache.put(updatedBlog.getId(),
        new BlogAggregationDTO(
            updatedBlog.getId(),
            new BlogDTO(
                updatedBlog.getBlogURL(),
                updatedBlog.getDescription(),
                updatedBlog.getName(),
                updatedBlog.getFeedURL(),
                updatedBlog.getPublishedDate(),
                List.of())
        )
    );
  }

  Mono<Void> deleteBlog(String id) {
    log.debug("Usuwam bloga o id {}", id);
    return blogRepository.getBlogWithCount(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(blog -> {
          if (blog.getBlogItemsCount() == 0) {
            return blogRepository.deleteById(blog.getBlogId())
                .doOnSuccess(v -> blogAggregationCache.invalidate(id));
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
    return Mono.justOrEmpty(blogAggregationCache.getIfPresent(id))
        .switchIfEmpty(Mono.defer(() -> blogRepository.findById(id).cache()
            .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
            .map(this::mapToBlogAggregationDto)
            .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id))
            .doOnSuccess(result -> blogAggregationCache.put(result.getBlogId(), result))));
  }

  private BlogAggregationDTO mapToBlogAggregationDto(Blog blog) {
    return new BlogAggregationDTO(
        blog.getId(),
        new BlogDTO(
            blog.getBlogURL(),
            blog.getDescription(),
            blog.getName(),
            blog.getFeedURL(),
            blog.getPublishedDate(),
            List.of()
        ));
  }

  public Flux<BlogAggregationDTO> getAllBlogDTOs() {
    log.debug("pobieram wszystkie blogi w postaci DTO");
    var dtoFlux = Flux.fromIterable(blogAggregationCache.asMap().values())
        .switchIfEmpty(Flux.defer(() -> blogRepository.getBlogsWithCount()
            .doOnNext(blog -> blogAggregationCache.put(blog.getBlogId(), blog)))
            .cache());
    return dtoFlux
        .doOnEach(blogDTO -> log.trace("getAllBlogDTOs {}", blogDTO));
  }

  void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
    blogAggregationCache.invalidateAll();
  }

  Mono<BlogDTO> updateBlog(BlogDTO blogDTO) {
    log.debug("Aktualizacja bloga {}", blogDTO.getLink());
    return getBlogByFeedUrl(blogDTO.getFeedURL())
        .flatMap(blog -> updateBlog(blog, blogDTO))
        .map(blog -> new BlogDTO(
            blog.getBlogURL(),
            blog.getDescription(),
            blog.getName(),
            blog.getFeedURL(),
            blog.getPublishedDate(),
            List.of()
        ));
  }

  private void addItemToBlog(Blog blog, ItemDTO item) {
    if (itemCache.getIfPresent(item.getLink()) == null) {
      itemCache.put(item.getLink(), item);
      producer.writeEventToQueue(new NewItemInBlogEvent(Instant.now(), item, blog.getId()));
      itemForSearchEventProducer.writeEventToQueue(new NewItemForSearchEvent(Instant.now(), item.getLink(), item.getTitle(), item.getDescription()));
    }
  }
}
