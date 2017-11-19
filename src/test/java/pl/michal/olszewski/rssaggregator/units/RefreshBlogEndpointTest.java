package pl.michal.olszewski.rssaggregator.units;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import pl.michal.olszewski.rssaggregator.api.RefreshBlogEndPoint;
import pl.michal.olszewski.rssaggregator.extenstions.MockitoExtension;
import pl.michal.olszewski.rssaggregator.service.UpdateBlogService;

@ExtendWith(MockitoExtension.class)
class RefreshBlogEndpointTest {

  private MockMvc mockMvc;

  @BeforeEach
  void configureSystemUnderTest() {
    UpdateBlogService updateBlogService = mock(UpdateBlogService.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new RefreshBlogEndPoint(updateBlogService))
        .build();
  }

  @Test
  void shouldRefreshBlog() throws Exception {
    mockMvc.perform(get("/api/v1/refresh").param("blogId", "1")
        .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isNoContent());
  }

}
