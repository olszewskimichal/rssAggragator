package pl.michal.olszewski.rssaggregator.integration;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

public abstract class ElasticIntegrationTestBase extends IntegrationTestBase {

  private static EmbeddedElastic embeddedElastic;

  protected void setupElastic() throws IOException, InterruptedException {
    if (embeddedElastic == null) {
      embeddedElastic = EmbeddedElastic.builder()
          .withElasticVersion("6.8.0")
          .withSetting(PopularProperties.HTTP_PORT, 9999)
          .withEsJavaOpts("-Xms128m -Xmx512m")
          .withDownloadUrl(new URL("https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.8.0.zip"))
          .withStartTimeout(1, TimeUnit.MINUTES)
          .withIndex("searchitems")
          .build()
          .start();
    }
  }


}