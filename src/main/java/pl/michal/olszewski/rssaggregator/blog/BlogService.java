package pl.michal.olszewski.rssaggregator.blog;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.item.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@Slf4j
class BlogService {

  private final BlogReactiveRepository blogRepository;
  private final MongoTemplate mongoTemplate;

  public BlogService(BlogReactiveRepository blogRepository, MongoTemplate mongoTemplate) {
    this.blogRepository = blogRepository;
    this.mongoTemplate = mongoTemplate;
  }

  @CacheEvict(value = {"blogs"}, allEntries = true)
  public Mono<Blog> createBlog(BlogDTO blogDTO) {
    log.debug("Tworzenie nowego bloga {}", blogDTO.getFeedURL());
    return blogRepository.findByFeedURL(blogDTO.getFeedURL())
        .switchIfEmpty(createBlogg(blogDTO));
  }

  private Mono<Blog> createBlogg(BlogDTO blogDTO) {
    log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());

    var blog = new Blog(blogDTO);
    blogDTO.getItemsList().stream()
        .map(Item::new)
        .forEach(v -> blog.addItem(v, mongoTemplate));
    return blogRepository.save(blog);
  }

  private Mono<Blog> getBlogByFeedUrl(String feedUrl) {
    log.debug("getBlogByFeedUrl {}", feedUrl);
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
                  .filter(v -> !linkSet.contains(v.getLink()))
                  .forEach(v -> blog.addItem(v, mongoTemplate));
              blog.updateFromDto(blogInfoFromRSS);
              return blogRepository.save(blog);
            }
        );
  }

  @CacheEvict(value = {"blogs"}, allEntries = true)
  public Mono<Void> deleteBlog(String id) {
    log.debug("Usuwam bloga o id {}", id);
    return blogRepository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(v -> {
          if (v.getItems().isEmpty()) {
            return blogRepository.delete(v);
          }
          v.deactive();
          return Mono.empty();
        });
  }

  @Transactional(readOnly = true)
  public Mono<BlogAggregationDTO> getBlogDTOById(String id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    return blogRepository.findById(id).cache()
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .map(BlogAggregationDTO::new)
        .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id));
  }

  @Cacheable("blogs")
  @Transactional(readOnly = true)
  public Flux<BlogAggregationDTO> getAllBlogDTOs() {
    log.debug("pobieram wszystkie blogi w postaci DTO ");
    var dtoFlux = blogRepository.getBlogsWithCount().cache();
    return dtoFlux
        .doOnEach(blogDTO -> log.trace("getAllBlogDTOs {}", blogDTO));
  }

  private List<BlogItemDTO> extractItems(Blog v) {
    return v.getItems().stream()
        .parallel()
        .map(BlogItemDTO::new)
        .collect(Collectors.toList());
  }

  @CacheEvict(value = {"blogs"}, allEntries = true)
  public void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
  }

  Mono<Blog> updateBlog(BlogDTO blogDTO) {
    return getBlogByFeedUrl(blogDTO.getFeedURL())
        .flatMap(blog -> updateBlog(blog, blogDTO));
  }

  Flux<BlogItemDTO> getBlogItemsForBlog(String blogId) {
    return blogRepository.findById(blogId)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(blogId)))
        .flatMapIterable(this::extractItems);
  }
}
