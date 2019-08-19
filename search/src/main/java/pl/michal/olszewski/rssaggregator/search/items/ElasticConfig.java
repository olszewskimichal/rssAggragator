package pl.michal.olszewski.rssaggregator.search.items;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

@Configuration
public class ElasticConfig {

  @Bean
  public ReactiveElasticsearchClient reactiveElasticsearchClient() {
    var configuration = ClientConfiguration.builder()
        .connectedTo("localhost:9200", "localhost:9300", "localhost:9999")
        .build();

    return ReactiveRestClients.create(configuration);
  }

  @Bean
  public ElasticsearchConverter elasticsearchConverter() {
    return new MappingElasticsearchConverter(elasticsearchMappingContext());
  }

  @Bean
  public SimpleElasticsearchMappingContext elasticsearchMappingContext() {
    return new SimpleElasticsearchMappingContext();
  }

  @Bean
  public ReactiveElasticsearchOperations reactiveElasticsearchOperations() {
    var resultsMapper = new ScoreResultsMapper();
    return new ReactiveElasticsearchTemplate(reactiveElasticsearchClient(), elasticsearchConverter(), resultsMapper);
  }

}