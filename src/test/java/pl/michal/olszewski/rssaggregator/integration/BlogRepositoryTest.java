package pl.michal.olszewski.rssaggregator.integration;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

import javax.persistence.PersistenceException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class BlogRepositoryTest {

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    private BlogRepository blogRepository;

    @Test
    public void shouldFindBlogByBlogURL() {
        //given
        Blog blog = new Blog("url", "", "", "", null);
        entityManager.persistAndFlush(blog);

        //when
        Optional<Blog> byBlogURL = blogRepository.findByBlogURL("url");

        //then
        assertThat(byBlogURL).isPresent();
    }

    @Test
    public void shouldFindBlogById() {
        //given
        Blog blog = new Blog("url", "", "", "", null);
        entityManager.persistAndFlush(blog);

        //when
        Optional<Blog> blogByID = blogRepository.findById(blog.getId());

        //then
        assertThat(blogByID).isPresent();
    }

    @Test
    public void shouldNotFindBlogByBlogURLWhenNotExists() {
        //when
        Optional<Blog> byBlogURL = blogRepository.findByBlogURL("url");

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
        Blog blog = new Blog("url", "", "", "", null);
        Blog theSameBlog = new Blog("url", "", "", "", null);
        //when
        entityManager.persistAndFlush(blog);
        //then
        assertThatThrownBy(()->entityManager.persistAndFlush(theSameBlog)).hasCauseInstanceOf(ConstraintViolationException.class).isInstanceOf(PersistenceException.class).hasMessageContaining("ConstraintViolationException");
    }

    @Test
    public void shouldThrowExceptionWhenItemDescriptionIsTooLong(){
        Blog blog = new Blog("url", "", "", "", null);
        String desc = IntStream.range(0, 10001).mapToObj(index -> "a").collect(Collectors.joining());
        blog.addItem(new Item(ItemDTO.builder().description(desc).build()));

        assertThatThrownBy(()->entityManager.persistAndFlush(blog))
                .hasCauseInstanceOf(DataException.class)
                .isInstanceOf(PersistenceException.class)
                .hasMessageContaining("DataException");

    }
}
