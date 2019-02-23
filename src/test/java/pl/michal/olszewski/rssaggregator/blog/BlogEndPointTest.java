package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@WebFluxTest(BlogEndPoint.class)
class BlogEndPointTest {

  @MockBean
  private BlogService blogService;

  @Autowired
  private WebTestClient webClient;

  @Test
  void shouldGetBlogByIdReturnStatusOK() {
    given(blogService.getBlogDTOById("1")).willReturn(Mono.just(new BlogInfoDTO()));

    webClient.get().uri("/api/v1/blogs/1")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void shouldGetBlogByIdReturnStatusNotFoundWhenBlogNotExist() {
    given(blogService.getBlogDTOById("1")).willThrow(new BlogNotFoundException("aaa"));  //TODO TO chyba powinno zwrocic mono.error

    webClient.get().uri("/api/v1/blogs/1")
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldGetBlogByIdReturnBlogAsJson() {
    given(blogService.getBlogDTOById("1")).willReturn(Mono.just(new BlogInfoDTO()));

    webClient.get().uri("/api/v1/blogs/1")
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
  }

  @Test
  void shouldReturnCorrectBlogById() {
    given(blogService.getBlogDTOById("1")).willReturn(Mono.just(new BlogInfoDTO(Blog.builder().name("nazwa").build())));

    webClient.get().uri("/api/v1/blogs/1")
        .exchange()
        .expectBody(BlogDTO.class)
        .value(v -> assertThat(v.getName()).isEqualTo("nazwa"));
  }

  @Test
  void shouldGetBlogsReturnStatusOK() {
    webClient.get().uri("/api/v1/blogs")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void shouldUpdateBlogReturnStatusNoContent() {
    webClient.put().uri("/api/v1/blogs")
        .body(BodyInserters.fromObject(BlogDTO.builder().build()))
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldCreateBlogReturnStatusNoContent() {
    webClient.post().uri("/api/v1/blogs")
        .body(BodyInserters.fromObject(BlogDTO.builder().build()))
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldDeleteBlogReturnStatusNoContent() {
    webClient.delete().uri("/api/v1/blogs/1")
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldGetBlogByNameReturnBlogAsJson() {
    given(blogService.getBlogDTOByName("name")).willReturn(Mono.just(new BlogInfoDTO()));

    webClient.get().uri("/api/v1/blogs/by-name/name")
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
  }

  @Test
  void shouldReturnCorrectBlogByName() {
    given(blogService.getBlogDTOByName("nazwa")).willReturn(Mono.just(new BlogInfoDTO(Blog.builder().name("nazwa").build())));

    webClient.get().uri("/api/v1/blogs/by-name/nazwa")
        .exchange()
        .expectBody(BlogDTO.class)
        .value(v -> assertThat(v.getName()).isEqualTo("nazwa"));
  }
}
