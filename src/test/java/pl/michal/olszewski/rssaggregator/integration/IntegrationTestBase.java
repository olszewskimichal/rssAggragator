package pl.michal.olszewski.rssaggregator.integration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.michal.olszewski.rssaggregator.config.Profiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Profiles.TEST)
@ExtendWith(SpringExtension.class)
public abstract class IntegrationTestBase {

  final TestRestTemplate template = new TestRestTemplate();
  @LocalServerPort
  public int port;
}