package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class BlogServiceIntegrationTest extends IntegrationTestBase {

  @Autowired
  private BlogService blogService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private BlogReactiveRepository blogRepository;

  @BeforeEach
  void setUp() {
    blogRepository.deleteAll().block();
  }

  @Test
  void shouldAggregateBlogPosts() {
    Blog blog = givenBlog().buildBlogWithItemsAndSave(2);

    Flux<BlogAggregationDTO> blogsWithCount = blogService.getBlogsWithCount();

    StepVerifier.create(blogsWithCount)
        .assertNext(v -> {
          System.err.println(v.toString());
          assertThat(v.getBlogItemsCount()).isEqualTo(2);
        })
        .verifyComplete();
  }

  @Test
  void shouldAggregateBlogPosts3() {
    Blog blog = givenBlog().buildBlogWithItemsAndSave(20);

    Flux<BlogAggregationDTO> blogsWithCount = blogService.getBlogsWithCount();

    StepVerifier.create(blogsWithCount)
        .assertNext(v -> {
          System.err.println(v.toString());
          assertThat(v.getBlogItemsCount()).isEqualTo(20);
        })
        .verifyComplete();
  }

  @Test
  void shouldAggregateBlogPosts2() {
    Blog blog = givenBlog().buildBlogWithItemsAndSave(0);

    Flux<BlogAggregationDTO> blogsWithCount = blogService.getBlogsWithCount();

    StepVerifier.create(blogsWithCount)
        .assertNext(v -> {
          System.err.println(v.toString());
          assertThat(v.getBlogItemsCount()).isEqualTo(0);
        })
        .verifyComplete();
  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository, mongoTemplate);
  }

}
