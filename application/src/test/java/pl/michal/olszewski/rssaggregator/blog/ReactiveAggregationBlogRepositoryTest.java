package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.item.ItemListFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class ReactiveAggregationBlogRepositoryTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private BlogReactiveRepository blogRepository;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "item");
    blogRepository.deleteAll().block();
  }

  @Test
  void shouldAggregateBlogWithItems() {
    Blog blog = givenBlog()
        .createAndSaveNewBlog();
    givenItems()
        .buildNumberOfItemsAndSave(2, blog.getId());

    Flux<BlogAggregationDTO> blogsWithCount = blogRepository.getBlogsWithCount();

    StepVerifier.create(blogsWithCount)
        .assertNext(aggregationDTO -> assertThat(aggregationDTO.getBlogItemsCount()).isEqualTo(2))
        .verifyComplete();
  }

  @Test
  void shouldAggregateBlogWithoutItems() {
    givenBlog()
        .createAndSaveNewBlog();

    Flux<BlogAggregationDTO> blogsWithCount = blogRepository.getBlogsWithCount();

    StepVerifier.create(blogsWithCount)
        .assertNext(aggregationDTO -> assertThat(aggregationDTO.getBlogItemsCount()).isEqualTo(0))
        .verifyComplete();
  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository);
  }

  private ItemListFactory givenItems() {
    return new ItemListFactory(mongoTemplate);
  }

}
