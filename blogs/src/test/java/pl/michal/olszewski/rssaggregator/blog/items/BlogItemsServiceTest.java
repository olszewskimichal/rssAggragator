package pl.michal.olszewski.rssaggregator.blog.items;

import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogReactiveRepository;
import pl.michal.olszewski.rssaggregator.item.Item;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlogItemsServiceTest {

  private BlogItemsService blogService;

  @Mock
  private BlogReactiveRepository blogRepository;

  @BeforeEach
  void setUp() {
    blogService = new BlogItemsService(blogRepository);
  }

  @Test
  void shouldThrownExceptionWhenGetItemsForNotExistingBlog() {
    //given
    given(blogRepository.findById("id")).willReturn(Mono.empty());

    //when
    StepVerifier.create(blogService.getBlogItemsForBlog("id"))
        .expectErrorMessage("Nie znaleziono bloga = id")
        .verify();
  }

  @Test
  void shouldReturnBlogItemsForBlog() {
    //given
    Blog blog = Blog.builder()
        .blogURL("url")
        .name("url")
        .item(new Item(ItemDTO.builder().title("title").build()))
        .build();

    given(blogRepository.findById("id")).willReturn(Mono.just(blog));

    //when
    StepVerifier.create(blogService.getBlogItemsForBlog("id"))
        .expectNextCount(1)
        .verifyComplete();
  }

}
