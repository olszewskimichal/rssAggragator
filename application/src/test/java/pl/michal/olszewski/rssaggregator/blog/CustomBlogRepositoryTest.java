package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.item.ItemListFactory;

class CustomBlogRepositoryTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private BlogRepository blogRepository;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "item");
    blogRepository.deleteAll();
  }

  @Test
  void shouldAggregateBlogWithItems() {
    Blog blog = givenBlog()
        .createAndSaveNewBlog();
    givenItems()
        .buildNumberOfItemsAndSave(2, blog.getId());

    Optional<BlogAggregationDTO> blogsWithCount = blogRepository.getBlogWithCount(blog.getId());

    assertThat(blogsWithCount).isPresent();
    assertThat(blogsWithCount.get().getBlogItemsCount()).isEqualTo(2L);
  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository);
  }

  private ItemListFactory givenItems() {
    return new ItemListFactory(mongoTemplate);
  }

}
