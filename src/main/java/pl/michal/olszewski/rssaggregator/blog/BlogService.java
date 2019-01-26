package pl.michal.olszewski.rssaggregator.blog;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    Blog blog = new Blog(blogDTO.getLink(), blogDTO.getDescription(), blogDTO.getName(), blogDTO.getFeedURL(), blogDTO.getPublishedDate(), null); //TODO za dluga linia
    blogDTO.getItemsList().stream()
        .map(Item::new)
        .forEach(v -> blog.addItem(v, itemRepository));
    return blogRepository.save(blog);
  }

  private Mono<Blog> getBlogByName(String name) {
    log.debug("getBlogByName {}", name);
    return blogRepository.findByName(name)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(name)));
  }

  private Mono<Blog> getBlogByFeedUrl(String feedUrl) {
    log.debug("getBlogByFeedUrl {}", feedUrl);
    return blogRepository.findByFeedURL(feedUrl)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(feedUrl)));
  }

  @Transactional
  public Mono<Blog> updateBlog(BlogDTO blogDTO) {
    return getBlogByFeedUrl(blogDTO.getFeedURL()).
        flatMap(
            blog -> {
              log.debug("aktualizuje bloga {}", blog.getName());
              Set<String> linkSet = blog.getItems().stream().parallel().map(Item::getLink).collect(Collectors.toSet());
              blogDTO.getItemsList().stream()
                  .map(Item::new)
                  .filter(v -> !linkSet.contains(v.getLink()))
                  .forEach(v -> blog.addItem(v, itemRepository));
              blog.updateFromDto(blogDTO);
              return blogRepository.save(blog);
            }
        );
  }

  @Cacheable("blogs")
  public Flux<Blog> getAllBlogs() {
    log.debug("Pobieram wszystkie blogi");
    return blogRepository.findAll();
  }

  @CacheEvict(value = {"blogs", "blogsName", "blogsDTO"}, allEntries = true)
  public Mono<Boolean> deleteBlog(String id) {
    log.debug("Usuwam bloga o id {}", id);
    Blog blog = blogRepository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .block(); //TODO Pozbvc sie blocka
    if (blog.getItems().isEmpty()) {
      blogRepository.delete(blog).block(); //TODO pozbyc sie bloka
      log.debug("usunalem blog {}", id);
      return Mono.just(true); //TODO nie wiem czy jest sens zwracac cos takiego
    } else {
      log.debug("Nie moglem usunac bloga wiec zmieniam jego aktywnosc {}", id);
      blog.deactive();
      return Mono.just(false);
    }
  }

  @Transactional(readOnly = true)
  public Mono<BlogDTO> getBlogDTOById(String id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    return blogRepository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .map(blogById -> (new BlogDTO(blogById.getBlogURL(), blogById.getDescription(), blogById.getName(), blogById.getFeedURL(), blogById.getPublishedDate(), extractItems(blogById)))) //TODO skrocic linie
        .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id));
  }

  @Cacheable("blogsName")
  @Transactional(readOnly = true)
  public Mono<BlogDTO> getBlogDTOByName(String name) {
    log.debug("pobieram bloga w postaci DTO o nazwie {} {}", name, clock.instant());
    return getBlogByName(name).map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), extractItems(v))) //TODO skrocic linie
        .doOnEach(blogDTO -> log.trace("getBlogDTOByName {}", blogDTO));
  }

  @Cacheable("blogsDTO")
  @Transactional(readOnly = true)
  public Flux<BlogDTO> getAllBlogDTOs(Integer limit) {
    log.debug("pobieram wszystkie blogi w postaci DTO z limitem {}", limit);
    Flux<BlogDTO> dtoFlux = getAllBlogs()
        .take(getLimit(limit))  //TODO refactor do Pageable
        .map(blog -> new BlogDTO(blog.getBlogURL(), blog.getDescription(), blog.getName(), blog.getFeedURL(), blog.getPublishedDate(), extractItems(blog))); //TODO skrocic linie
    return dtoFlux.doOnEach(blogDTO -> log.trace("getAllBlogDTOs {}", blogDTO));
  }

  private List<ItemDTO> extractItems(Blog v) {
    return v.getItems().stream().parallel().map(item -> new ItemDTO(item.getTitle(), item.getDescription(), item.getLink(), item.getDate(), item.getAuthor())).collect(Collectors.toList()); //TODO skrocic linie
  }

  private int getLimit(final Integer size) {
    return (Objects.isNull(size) ? 20 : size);
  }

  @CacheEvict(value = {"blogs", "blogsDTO", "blogsName"}, allEntries = true)
  public void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
  }
}
