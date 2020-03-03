package pl.michal.olszewski.rssaggregator;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication()
@EnableCaching
@EnableAsync
@EntityScan(
    basePackageClasses = {RssAggregatorApplication.class}
)
@EnableScheduling
@EnableConfigurationProperties
@EnableJms
@EnableMongoRepositories(
    basePackages = {
        "pl.michal.olszewski.rssaggregator.item",
        "pl.michal.olszewski.rssaggregator.blog",
        "pl.michal.olszewski.rssaggregator.cache"
    }
)
public class RssAggregatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(RssAggregatorApplication.class, args);
  }

  @Profile({"prod"})
  @Bean
  @Primary
  public Executor threadPoolTaskExecutorProd() {
    var threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(50);
    threadPoolTaskExecutor.setMaxPoolSize(100);
    return threadPoolTaskExecutor;
  }

  @Profile({"development"})
  @Bean
  @Primary
  public Executor threadPoolTaskExecutorDevelopment() {
    var threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(8);
    threadPoolTaskExecutor.setMaxPoolSize(16);
    return threadPoolTaskExecutor;
  }

  @Profile({"test"})
  @Bean
  @Primary
  public Executor testThreadPoolTaskExecutor() {
    return Executors.newSingleThreadExecutor();
  }

}

