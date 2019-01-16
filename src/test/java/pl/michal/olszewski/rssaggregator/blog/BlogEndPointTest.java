package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
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

  private static byte[] convertObjectToJsonBytes(Object object) throws IOException {
    ObjectMapper mapper = objectMapper();
    return mapper.writeValueAsBytes(object);
  }

  private static ObjectMapper objectMapper() {
    return new ObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Test
  void shouldGetBlogByIdReturnStatusOK() throws Exception {
    given(blogService.getBlogDTOById(1L)).willReturn(Mono.just(new BlogDTO()));

    webClient.get().uri("/api/v1/blogs/1")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void shouldGetBlogByIdReturnStatusNotFoundWhenBlogNotExist() throws Exception {
    given(blogService.getBlogDTOById(1L)).willThrow(new BlogNotFoundException("aaa"));

    webClient.get().uri("/api/v1/blogs/1")
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldGetBlogByIdReturnBlogAsJson() throws Exception {
    given(blogService.getBlogDTOById(1L)).willReturn(Mono.just(new BlogDTO()));

    webClient.get().uri("/api/v1/blogs/1")
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
  }

  @Test
  void shouldReturnCorrectBlogById() throws Exception {
    given(blogService.getBlogDTOById(1L)).willReturn(Mono.just(BlogDTO.builder().name("nazwa").build()));

    webClient.get().uri("/api/v1/blogs/1")
        .exchange()
        .expectBody(BlogDTO.class)
        .value(v -> assertThat(v.getName()).isEqualTo("nazwa"));
  }

  @Test
  void shouldGetBlogsReturnStatusOK() throws Exception {
    webClient.get().uri("/api/v1/blogs")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void shouldUpdateBlogReturnStatusNoContent() throws Exception {
    webClient.put().uri("/api/v1/blogs")
        .body(BodyInserters.fromObject(BlogDTO.builder().build()))
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldCreateBlogReturnStatusNoContent() throws Exception {

    webClient.post().uri("/api/v1/blogs")
        .body(BodyInserters.fromObject(BlogDTO.builder().build()))
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldDeleteBlogReturnStatusNoContent() throws Exception {
    webClient.delete().uri("/api/v1/blogs/1")
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldGetBlogByNameReturnBlogAsJson() throws Exception {
    given(blogService.getBlogDTOByName("name")).willReturn(Mono.just(new BlogDTO()));

    webClient.get().uri("/api/v1/blogs/by-name/name")
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
  }

  @Test
  void shouldReturnCorrectBlogByName() throws Exception {
    given(blogService.getBlogDTOByName("nazwa")).willReturn(Mono.just(BlogDTO.builder().name("nazwa").build()));

    webClient.get().uri("/api/v1/blogs/by-name/nazwa")
        .exchange()
        .expectBody(BlogDTO.class)
        .value(v -> assertThat(v.getName()).isEqualTo("nazwa"));
  }
}
