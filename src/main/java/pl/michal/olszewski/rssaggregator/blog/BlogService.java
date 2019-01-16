package pl.michal.olszewski.rssaggregator.blog;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

  private final BlogRepository blogRepository;
  private final Clock clock;

  public BlogService(BlogRepository blogRepository, Clock clock) {
    this.blogRepository = blogRepository;
    this.clock = clock;
  }

  @CacheEvict(value = {"blogs", "blogsName", "blogsDTO"}, allEntries = true)
  public Mono<Blog> createBlog(BlogDTO blogDTO) {
    Optional<Blog> byFeedURL = blogRepository.findByFeedURL(blogDTO.getFeedURL());
    if (!byFeedURL.isPresent()) {
      log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());

      Blog blog = new Blog(blogDTO.getLink(), blogDTO.getDescription(), blogDTO.getName(), blogDTO.getFeedURL(), blogDTO.getPublishedDate(), null);
      blogDTO.getItemsList().stream()
          .map(Item::new)
          .forEach(blog::addItem);
      blogRepository.save(blog);
      return Mono.just(blog);
    }
    return Mono.just(byFeedURL.get());
  }

  private Mono<Blog> getBlogByName(String name) {
    return Mono.just(blogRepository.findByName(name).orElseThrow(() -> new BlogNotFoundException(name)));
  }

  private Mono<Blog> getBlogByFeedUrl(String feedUrl) {
    return Mono.just(blogRepository.findByFeedURL(feedUrl).orElseThrow(() -> new BlogNotFoundException(feedUrl)));
  }

  @Transactional
  public Mono<Blog> updateBlog(BlogDTO blogDTO) {
    Blog blog = getBlogByFeedUrl(blogDTO.getFeedURL()).block();   //TODO poprawic

    Set<String> linkSet = blog.getItems().stream().parallel().map(Item::getLink).collect(Collectors.toSet());

    blogDTO.getItemsList().stream()
        .map(Item::new)
        .filter(v -> !linkSet.contains(v.getLink()))
        .forEach(blog::addItem);
    blog.updateFromDto(blogDTO);
    return Mono.just(blog);
  }

  @Cacheable("blogs")
  public Flux<Blog> getAllBlogs() {
    log.debug("Pobieram wszystkie blogi");
    List<Blog> all = blogRepository.findAll();
    log.debug("Znalazlem w bazie {} blogow", all.size());
    log.trace("Wszystkie blogi to {}", all);
    return Flux.fromIterable(all);
  }

  @CacheEvict(value = {"blogs", "blogsName", "blogsDTO"}, allEntries = true)
  public Mono<Boolean> deleteBlog(Long id) {
    log.debug("Usuwam bloga o id {}", id);
    Blog blog = blogRepository.findById(id).orElseThrow(() -> new BlogNotFoundException(id));
    if (blog.getItems().isEmpty()) {
      blogRepository.delete(blog);
      return Mono.just(true);
    } else {
      blog.deactive();
      return Mono.just(false);
    }
  }

  @Transactional(readOnly = true)
  public Mono<BlogDTO> getBlogDTOById(Long id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    Blog blogById = blogRepository.findById(id).orElseThrow(() -> new BlogNotFoundException(id));
    return Mono.just(new BlogDTO(blogById.getBlogURL(), blogById.getDescription(), blogById.getName(), blogById.getFeedURL(), blogById.getPublishedDate(), extractItems(blogById)));
  }

  @Cacheable("blogsName")
  @Transactional(readOnly = true)
  public Mono<BlogDTO> getBlogDTOByName(String name) {
    log.debug("pobieram bloga w postaci DTO o nazwie {} {}", name, clock.instant());
    return getBlogByName(name).map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), extractItems(v)));
  }

  @Cacheable("blogsDTO")
  @Transactional(readOnly = true)
  public Flux<BlogDTO> getAllBlogDTOs(Integer limit) {
    log.debug("pobieram wszystkie blogi w postaci DTO z limitem {}", limit);
    Flux<BlogDTO> dtoFlux = getAllBlogs()
        .take(getLimit(limit)) //TODO jakos to wyprostowac
        .map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), extractItems(v)));
    return dtoFlux.doOnEach(v -> log.debug(v.toString()));
  }

  private List<ItemDTO> extractItems(Blog v) {
    return v.getItems().stream().parallel().map(item -> new ItemDTO(item.getTitle(), item.getDescription(), item.getLink(), item.getDate(), item.getAuthor())).collect(Collectors.toList());
  }

  private int getLimit(final Integer size) {
    return (Objects.isNull(size) ? 20 : size);
  }

  @CacheEvict(value = {"blogs", "blogsDTO", "blogsName"}, allEntries = true)
  public void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
  }
}
