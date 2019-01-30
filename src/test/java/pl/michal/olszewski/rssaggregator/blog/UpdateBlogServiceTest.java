package pl.michal.olszewski.rssaggregator.blog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBlogServiceTest {

    private UpdateBlogService updateBlogService;

    @Mock
    private BlogReactiveRepository blogRepository;
    @Mock
    private AsyncService asyncService;

    @BeforeEach
    void setUp() {
        updateBlogService = new UpdateBlogService(blogRepository, asyncService, Executors.newSingleThreadExecutor());
    }

    @Test
    void shouldNotUpdateBlogFromId() {
        //given
        given(blogRepository.findById("1")).willReturn(Mono.empty());
        //when
        //then
        assertThatThrownBy(() -> updateBlogService.refreshBlogFromId("1")).hasMessageContaining("Nie znaleziono bloga");
    }

    @Test
    void shouldUpdateBlogFromId() {
        //given
        given(blogRepository.findById("1")).willReturn(Mono.just(new Blog()));
        //when
        updateBlogService.refreshBlogFromId("1");

        verify(blogRepository, times(1)).findById("1");
        verify(asyncService, times(1)).updateBlog(new Blog());
        verifyNoMoreInteractions(blogRepository);
    }

    @Test
    void shouldRunUpdatesForAllBlogs() {
        //given
        ReflectionTestUtils.setField(updateBlogService, "enableJob", true);
        given(blogRepository.findAll()).willReturn(Flux.just(new Blog()));
        //when
        StepVerifier.create(updateBlogService.updateAllActiveBlogs())
            .expectNext(Collections.singletonList(Collections.singletonList(false)))
            .expectComplete()
            .verify();

        verify(blogRepository, times(1)).findAll();
        verify(asyncService, times(1)).updateBlog(new Blog());
        verifyNoMoreInteractions(blogRepository);
    }

    @Test
    void shouldNotRunUpdatesWhenIsNotEnabled() {
        //given
        ReflectionTestUtils.setField(updateBlogService, "enableJob", false);
        //when
        StepVerifier.create(updateBlogService.updateAllActiveBlogs())
            .expectComplete()
            .verify();

        verify(blogRepository, times(0)).findAll();
        verify(asyncService, times(0)).updateBlog(new Blog());
        verifyNoMoreInteractions(blogRepository);
    }
}