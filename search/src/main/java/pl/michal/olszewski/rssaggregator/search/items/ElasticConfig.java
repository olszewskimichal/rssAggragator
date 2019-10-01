package pl.michal.olszewski.rssaggregator.search.items;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
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
  public ReactiveElasticsearchOperations reactiveElasticsearchOperations(@Value("${elasticNode}") final String elasticNode) {
    var resultsMapper = new ScoreResultsMapper();
    return new ReactiveElasticsearchTemplate(reactiveElasticsearchClient(elasticNode), elasticsearchConverter(), resultsMapper);
  }

  @Bean
  RestHighLevelClient client(@Value("${elasticNode}") final String elasticNode) {
    var clientConfiguration = ClientConfiguration.builder()
        .connectedTo(elasticNode)
        .build();

    return RestClients.create(clientConfiguration).rest();
  }

  @Bean
  public ElasticsearchConverter elasticsearchConverter() {
    return new MappingElasticsearchConverter(elasticsearchMappingContext());
  }

  @Bean
  public SimpleElasticsearchMappingContext elasticsearchMappingContext() {
    return new SimpleElasticsearchMappingContext();
  }

  private ReactiveElasticsearchClient reactiveElasticsearchClient(final String elasticNode) {
    var configuration = ClientConfiguration.builder()
        .connectedTo(elasticNode)
        .build();

    return ReactiveRestClients.create(configuration);
  }

}