package pl.michal.olszewski.rssaggregator.blog;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
class HttpBlogValidation implements BlogValidation {

  @Override
  public void validate(CreateBlogDTO createBlogDTO) {
    if (createBlogDTO.getLink() == null || createBlogDTO.getFeedURL() == null) {
      throw new IncompleteBlogException();
    }

    HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    sendRequest(client, createBlogDTO.getLink());
    sendRequest(client, createBlogDTO.getFeedURL());
  }

  private void sendRequest(HttpClient client, String url) {
    try {
      HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
      client.send(request, BodyHandlers.discarding());
    } catch (IOException | InterruptedException | IllegalArgumentException e) {
      throw new IncorrectUrlException(String.format("Nie mogę się połączyć z adresem %s, sprawdz poprawność tego adresu lub spróbuj ponownie za chwilę", url));
    }
  }
}
