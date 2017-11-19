package pl.michal.olszewski.rssaggregator.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.michal.olszewski.rssaggregator.config.Profiles;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.factory.BlogListFactory;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

@DataJpaTest
@ActiveProfiles(Profiles.TEST)
@RunWith(JUnitPlatform.class)
@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
public class BlogRepositoryTest {

  @Autowired
  protected TestEntityManager entityManager;

  @Autowired
  private BlogRepository blogRepository;

  @Test
  public void shouldFindBlogByBlogURL() {
    //given
    givenBlog()
        .withURL("url");

    //when
    Optional<Blog> byBlogURL = blogRepository.findByFeedURL("url");

    //then
    assertThat(byBlogURL).isPresent();
  }

  @Test
  public void shouldFindBlogById() {
    //given
    Blog blog = givenBlog()
        .withURL("url");

    //when
    Optional<Blog> blogByID = blogRepository.findById(blog.getId());

    //then
    assertThat(blogByID).isPresent();
  }

  @Test
  public void shouldNotFindBlogByBlogURLWhenNotExists() {
    //when
    Optional<Blog> byBlogURL = blogRepository.findByFeedURL("url");

    //then
    assertThat(byBlogURL).isNotPresent();
  }

  @Test
  public void shouldNotFindBlogByIdWhenNotExists() {
    //when
    Optional<Blog> blogById = blogRepository.findById(1L);

    //then
    assertThat(blogById).isNotPresent();
  }

  @Test
  public void shouldThrownExceptionWhenSave2BlogWithTheSameName() {
    //given
    givenBlog()
        .withURL("url");
    //then
    assertThatThrownBy(() -> entityManager.persistAndFlush(new Blog("url", "", "", "", null))).hasCauseInstanceOf(ConstraintViolationException.class).isInstanceOf(PersistenceException.class)
        .hasMessageContaining("ConstraintViolationException");
  }

  @Test
  public void shouldThrowExceptionWhenItemDescriptionIsTooLong() {
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
  public void shouldFindBlogByName() {
    //given
    givenBlog()
        .withName("url");

    //when
    Optional<Blog> byName = blogRepository.findByName("url");

    //then
    assertThat(byName).isPresent();
  }

  @Test
  public void shouldNotFindBlogByNameWhenNotExists() {
    //when
    Optional<Blog> byName = blogRepository.findByName("name");

    //then
    assertThat(byName).isNotPresent();
  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository);
  }

}
