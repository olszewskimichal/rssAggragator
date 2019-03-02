package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class UpdateBlogServiceTest {

  private UpdateBlogService updateBlogService;

  @Mock
  private BlogReactiveRepository blogRepository;
  @Mock
  private AsyncService asyncService;

  @BeforeEach
  void setUp() {
    updateBlogService = new UpdateBlogService(blogRepository, asyncService, Executors.newSingleThreadExecutor(), null);
  }

  @Test
  void shouldNotUpdateBlogFromId() {
    //given
    given(blogRepository.findById("1")).willReturn(Mono.empty());
    //when
    //then
    assertThatThrownBy(() -> updateBlogService.refreshBlogFromId("1", "correlationID")).hasMessageContaining("Nie znaleziono bloga");
  }

  @Test
  void shouldUpdateBlogFromId() {
    //given
    given(blogRepository.findById("1")).willReturn(Mono.just(new Blog()));
    //when
    updateBlogService.refreshBlogFromId("1", "correlationID");

    verify(blogRepository, times(1)).findById("1");
    verify(asyncService, times(1)).updateBlog(Mockito.eq(new Blog()), Mockito.anyString());
    verifyNoMoreInteractions(blogRepository);
  }
}