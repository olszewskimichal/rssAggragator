package pl.michal.olszewski.rssaggregator.blog;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.michal.olszewski.rssaggregator.blog.BlogEndPoint;
import pl.michal.olszewski.rssaggregator.blog.BlogDTO;
import pl.michal.olszewski.rssaggregator.extenstions.MockitoExtension;
import pl.michal.olszewski.rssaggregator.blog.BlogService;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class BlogEndPointTest {

  private BlogService blogService;
  private MockMvc mockMvc;

  private static byte[] convertObjectToJsonBytes(Object object) throws IOException {
    ObjectMapper mapper = objectMapper();
    return mapper.writeValueAsBytes(object);
  }

  private static ObjectMapper objectMapper() {
    return new ObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @BeforeEach
  void configureSystemUnderTest() {
    blogService = mock(BlogService.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new BlogEndPoint(blogService))
        .build();
  }

  @Test
  void shouldGetBlogByIdReturnStatusOK() throws Exception {
    given(blogService.getBlogDTOById(1L)).willReturn(Mono.just(new BlogDTO()));

    mockMvc.perform(get("/api/v1/blogs/1"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldGetBlogByIdReturnStatusNotFoundWhenBlogNotExist() throws Exception {
    given(blogService.getBlogDTOById(1L)).willReturn(null);

    mockMvc.perform(get("/api/v1/blogs/1"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldGetBlogByIdReturnBlogAsJson() throws Exception {
    given(blogService.getBlogDTOById(1L)).willReturn(Mono.just(new BlogDTO()));

    mockMvc.perform(get("/api/v1/blogs/1"))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
  }

  @Test
  void shouldReturnCorrectBlogById() throws Exception {
    given(blogService.getBlogDTOById(1L)).willReturn(Mono.just(BlogDTO.builder().name("nazwa").build()));

    mockMvc.perform(get("/api/v1/blogs/1"))
        .andExpect(jsonPath("$.name", is("nazwa")));
  }

  @Test
  void shouldGetBlogsReturnStatusOK() throws Exception {
    mockMvc.perform(get("/api/v1/blogs"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldUpdateBlogReturnStatusNoContent() throws Exception {
    mockMvc.perform(put("/api/v1/blogs")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(convertObjectToJsonBytes(BlogDTO.builder().build())))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldCreateBlogReturnStatusNoContent() throws Exception {
    mockMvc.perform(post("/api/v1/blogs")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(convertObjectToJsonBytes(BlogDTO.builder().build())))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldDeleteBlogReturnStatusNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/blogs/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldGetBlogByNameReturnBlogAsJson() throws Exception {
    given(blogService.getBlogDTOByName("name")).willReturn(Mono.just(new BlogDTO()));

    mockMvc.perform(get("/api/v1/blogs/by-name/name"))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
  }

  @Test
  void shouldReturnCorrectBlogByName() throws Exception {
    given(blogService.getBlogDTOByName("nazwa")).willReturn(Mono.just(BlogDTO.builder().name("nazwa").build()));

    mockMvc.perform(get("/api/v1/blogs/by-name/nazwa"))
        .andExpect(jsonPath("$.name", is("nazwa")));
  }
}
