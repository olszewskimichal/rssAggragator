package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.michal.olszewski.rssaggregator.config.Profiles;
import pl.michal.olszewski.rssaggregator.item.Item;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;

@DataJpaTest
@ActiveProfiles(Profiles.TEST)
public class BlogRepositoryTest {

  @Autowired
  protected TestEntityManager entityManager;

  @Autowired
  private BlogRepository blogRepository;

  @Test
  void shouldFindBlogByBlogURL() {
    //given
    givenBlog()
        .withURL("url");

    //when
    Optional<Blog> byBlogURL = blogRepository.findByFeedURL("url");

    //then
    assertThat(byBlogURL).isPresent();
  }

  @Test
  void shouldFindBlogById() {
    //given
    Blog blog = givenBlog()
        .withURL("url");

    //when
    Optional<Blog> blogByID = blogRepository.findById(blog.getId());

    //then
    assertThat(blogByID).isPresent();
  }

  @Test
  void shouldNotFindBlogByBlogURLWhenNotExists() {
    //when
    Optional<Blog> byBlogURL = blogRepository.findByFeedURL("url");

    //then
    assertThat(byBlogURL).isNotPresent();
  }

  @Test
  void shouldNotFindBlogByIdWhenNotExists() {
    //when
    Optional<Blog> blogById = blogRepository.findById(1L);

    //then
    assertThat(blogById).isNotPresent();
  }

  @Test
  void shouldThrownExceptionWhenSave2BlogWithTheSameName() {
    //given
    givenBlog()
        .withURL("url");
    //then
    assertThatThrownBy(() -> entityManager.persistAndFlush(new Blog("url", "", "", "", null, null))).hasCauseInstanceOf(ConstraintViolationException.class).isInstanceOf(PersistenceException.class)
        .hasMessageContaining("ConstraintViolationException");
  }

  @Test
  void shouldThrowExceptionWhenItemDescriptionIsTooLong() {
    Blog blog = givenBlog()
        .withURL("url");
    String desc = IntStream.range(0, 10001).parallel().mapToObj(index -> "a").collect(Collectors.joining());
    blog.addItem(new Item(ItemDTO.builder().description(desc).build()));

    assertThatThrownBy(() -> entityManager.persistAndFlush(blog))
        .hasCauseInstanceOf(DataException.class)
        .isInstanceOf(PersistenceException.class)
        .hasMessageContaining("DataException");

  }

  @Test
  void shouldFindBlogByName() {
    //given
    givenBlog()
        .withName("url");

    //when
    Optional<Blog> byName = blogRepository.findByName("url");

    //then
    assertThat(byName).isPresent();
  }

  @Test
  void shouldNotFindBlogByNameWhenNotExists() {
    //when
    Optional<Blog> byName = blogRepository.findByName("name");

    //then
    assertThat(byName).isNotPresent();
  }

  @Test
  void shouldGetAllBlogsIfAllAreActive() {
    givenBlog().buildNumberOfBlogsAndSave(5);
    //when
    List<Blog> streamAll = blogRepository.findStreamAll();
    //then
    assertThat(streamAll).hasSize(5);
  }

  @Test
  void shouldNotReturnNotActiveBlog() {
    givenBlog().notActive();
    //when
    List<Blog> streamAll = blogRepository.findStreamAll();
    //then
    assertThat(streamAll).hasSize(0);
  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository);
  }

}
