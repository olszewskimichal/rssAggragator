package pl.michal.olszewski.rssaggregator.item;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static pl.michal.olszewski.rssaggregator.item.ReadItemDTO.builder;
import static reactor.core.publisher.Mono.empty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(ReadItemController.class)
class ReadItemControllerTest {

  @MockBean
  private ReadItemService readItemService;

  @Autowired
  private WebTestClient webClient;

  @Test
  void shouldReturnNoContentWhenMarkItemSuccess() {
    var readItemDTO = builder().itemId("itemId").read(true).build();

    given(readItemService.processRequest(eq(readItemDTO))).willReturn(empty());

    webClient.post().uri("/api/v1/items/mark")
        .contentType(APPLICATION_JSON)
        .body(fromObject(readItemDTO))
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldReturnNotFoundWhenMarkItemThatNotExists() {
    var readItemDTO = builder().itemId("itemId2").read(true).build();

    given(readItemService.processRequest(eq(readItemDTO))).willReturn(Mono.error(new ItemNotFoundException("")));

    webClient.post().uri("/api/v1/items/mark")
        .contentType(APPLICATION_JSON)
        .body(fromObject(readItemDTO))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

}
