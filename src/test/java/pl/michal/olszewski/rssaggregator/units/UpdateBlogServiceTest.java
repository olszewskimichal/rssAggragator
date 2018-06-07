package pl.michal.olszewski.rssaggregator.units;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.extenstions.MockitoExtension;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;
import pl.michal.olszewski.rssaggregator.service.AsyncService;
import pl.michal.olszewski.rssaggregator.service.UpdateBlogService;

@ExtendWith(MockitoExtension.class)
class UpdateBlogServiceTest {

  private UpdateBlogService updateBlogService;

  @Mock
  private BlogRepository blogRepository;
  @Mock
  private AsyncService asyncService;

  @BeforeEach
  void setUp() {
    updateBlogService = new UpdateBlogService(blogRepository, asyncService);
  }

  @Test
  void shouldNotUpdateBlogFromId() {
    //given
    given(blogRepository.findById(1L)).willReturn(Optional.empty());
    //when
    //then
    assertThatThrownBy(() -> updateBlogService.updateBlogFromId(1L)).hasMessageContaining("Nie znaleziono bloga");
  }

  @Test
  void shouldUpdateBlogFromId() {
    //given
    given(blogRepository.findById(1L)).willReturn(Optional.of(new Blog()));
    //when
    updateBlogService.updateBlogFromId(1L);

    verify(blogRepository, times(1)).findById(1L);
    verify(asyncService, times(1)).updateBlog(new Blog());
    verifyNoMoreInteractions(blogRepository);
  }

  @Test
  void shouldRunUpdatesForAllBlogs() {
    //given
    ReflectionTestUtils.setField(updateBlogService, "enableJob", true);
    given(blogRepository.findStreamAll()).willReturn(Arrays.asList(new Blog()));
    //when
    updateBlogService.updatesBlogs();

    verify(blogRepository, times(1)).findStreamAll();
    verify(asyncService, times(1)).updateBlog(new Blog());
    verifyNoMoreInteractions(blogRepository);
  }

  @Test
  void shouldNotRunUpdatesWhenIsNotEnabled() {
    //given
    ReflectionTestUtils.setField(updateBlogService, "enableJob", false);
    //when
    updateBlogService.updatesBlogs();

    verify(blogRepository, times(0)).findStreamAll();
    verify(asyncService, times(0)).updateBlog(new Blog());
    verifyNoMoreInteractions(blogRepository);
  }
}