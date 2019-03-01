package pl.michal.olszewski.rssaggregator.blog;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
  private final Map<String, BlogAggregationDTO> cache;

    public BlogService(BlogReactiveRepository blogRepository, MongoTemplate mongoTemplate, Map<String, BlogAggregationDTO> cache) {
        this.blogRepository = blogRepository;
        this.mongoTemplate = mongoTemplate;
        this.cache = cache;
    }

    Mono<Blog> getBlogOrCreate(BlogDTO blogDTO) {
        log.debug("Tworzenie nowego bloga {}", blogDTO.getFeedURL());
        return blogRepository.findByFeedURL(blogDTO.getFeedURL())
            .switchIfEmpty(Mono.defer(() -> createBlog(blogDTO)));
    }

    private Mono<Blog> createBlog(BlogDTO blogDTO) {
        log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());
        var blog = new Blog(blogDTO);
        blogDTO.getItemsList().stream()
            .map(Item::new)
            .forEach(v -> blog.addItem(v, mongoTemplate));
        return blogRepository.save(blog)
            .doOnNext(createdBlog -> cache.putIfAbsent(createdBlog.getId(), new BlogAggregationDTO(createdBlog)));
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
                .doOnSuccess(v -> cache.remove(id));
          }
          blog.deactive();
          return Mono.empty();
        });
  }

    @Transactional(readOnly = true)
    public Mono<BlogAggregationDTO> getBlogDTOById(String id) {
        log.debug("pobieram bloga w postaci DTO o id {}", id);
        return Mono.justOrEmpty(cache.get(id))
            .switchIfEmpty(Mono.defer(() -> blogRepository.findById(id).cache()
                .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
                .map(BlogAggregationDTO::new)
                .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id))
                .doOnSuccess(v -> cache.putIfAbsent(id, v))));
    }

    @Transactional(readOnly = true)
    public Flux<BlogAggregationDTO> getAllBlogDTOs() {
        log.debug("pobieram wszystkie blogi w postaci DTO ");
        var dtoFlux = Flux.fromIterable(cache.values())
            .switchIfEmpty(Flux.defer(() -> blogRepository.getBlogsWithCount()
                .doOnNext(blog -> cache.putIfAbsent(blog.getBlogId(), blog)))
                .cache());
        return dtoFlux
            .doOnEach(blogDTO -> log.trace("getAllBlogDTOs {}", blogDTO));
    }

  private List<BlogItemDTO> extractItems(Blog v) {
    return v.getItems().stream()
        .parallel()
        .map(BlogItemDTO::new)
        .collect(Collectors.toList());
  }

  void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
    cache.clear();
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
