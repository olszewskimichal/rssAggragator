package pl.michal.olszewski.rssaggregator.blog;

import java.time.Clock;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.item.Item;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@Slf4j
class BlogService {

  private final BlogReactiveRepository blogRepository;
  private final MongoTemplate itemRepository;
  private final Clock clock;

  public BlogService(BlogReactiveRepository blogRepository, Clock clock, MongoTemplate itemRepository) {
    this.blogRepository = blogRepository;
    this.clock = clock;
    this.itemRepository = itemRepository;
  }

  @CacheEvict(value = {"blogs", "blogsDTO"}, allEntries = true)
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
        .forEach(v -> blog.addItem(v, itemRepository));
    return blogRepository.save(blog);
  }

  private Mono<Blog> getBlogByName(String name) {
    log.debug("getBlogByName {}", name);
    return blogRepository.findByName(name).cache()
        .switchIfEmpty(Mono.error(new BlogNotFoundException(name)));
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
                  .forEach(v -> blog.addItem(v, itemRepository));
              blog.updateFromDto(blogInfoFromRSS);
              return blogRepository.save(blog);
            }
        );
  }

  private Flux<Blog> getBlogs(Integer limit) {
    if (limit != null) {
      return getBlogsWithLimit(limit);
    }
    return getAllBlogs();
  }

  @Cacheable("blogs")
  public Flux<Blog> getAllBlogs() {
    log.debug("Pobieram wszystkie blogi");
    return blogRepository.findAll().cache();
  }

  private Flux<Blog> getBlogsWithLimit(int limit) {
    log.debug("Pobieram {} blogow", limit);
    return blogRepository.findAll(PageRequest.of(0, limit));
  }

  @CacheEvict(value = {"blogs", "blogsName", "blogsDTO"}, allEntries = true)
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
  public Mono<BlogDTO> getBlogDTOById(String id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    return blogRepository.findById(id).cache()
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .map(blogById -> (new BlogDTO(blogById, extractItems(blogById))))
        .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id));
  }

  @Cacheable("blogsName")
  @Transactional(readOnly = true)
  public Mono<BlogDTO> getBlogDTOByName(String name) {
    log.debug("pobieram bloga w postaci DTO o nazwie {} {}", name, clock.instant());
    return getBlogByName(name)
        .map(blog -> new BlogDTO(blog, extractItems(blog)))
        .doOnEach(blogDTO -> log.trace("getBlogDTOByName {}", blogDTO));
  }

  @Cacheable("blogsDTO")
  @Transactional(readOnly = true)
  public Flux<BlogDTO> getAllBlogDTOs(Integer limit) {
    log.debug("pobieram wszystkie blogi w postaci DTO z limitem {}", limit);
    var dtoFlux = getBlogs(limit)
        .map(blog -> new BlogDTO(blog.getBlogURL(), blog.getDescription(), blog.getName(), blog.getFeedURL(), blog.getPublishedDate(), extractItems(blog)));
    return dtoFlux
        .doOnEach(blogDTO -> log.trace("getAllBlogDTOs {}", blogDTO));
  }

  private List<ItemDTO> extractItems(Blog v) {
    return v.getItems().stream()
        .parallel()
        .map(ItemDTO::new)
        .collect(Collectors.toList());
  }

  @CacheEvict(value = {"blogs", "blogsDTO", "blogsName"}, allEntries = true)
  public void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
  }

  Mono<Blog> updateBlog(BlogDTO blogDTO) {
    return getBlogByFeedUrl(blogDTO.getFeedURL())
        .flatMap(blog -> updateBlog(blog, blogDTO));
  }
}
