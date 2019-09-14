package pl.michal.olszewski.rssaggregator.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

  @LocalServerPort
  public int port;

  final protected WebTestClient webTestClient = WebTestClient
      .bindToServer()
      .baseUrl("http://localhost:" + port)
      .build();

}