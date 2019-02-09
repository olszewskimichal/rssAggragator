package pl.michal.olszewski.rssaggregator;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.michal.olszewski.rssaggregator.config.Profiles;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EntityScan(
    basePackageClasses = {RssAggregatorApplication.class}
)
@EnableScheduling
@EnableConfigurationProperties
public class RssAggregatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(RssAggregatorApplication.class, args);
  }

  @Profile({Profiles.PRODUCTION})
  @Bean
  @Primary
  public Executor threadPoolTaskExecutorProd() {
    var threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(20);
    threadPoolTaskExecutor.setMaxPoolSize(40);
    return threadPoolTaskExecutor;
  }

  @Profile({Profiles.DEVELOPMENT})
  @Bean
  @Primary
  public Executor threadPoolTaskExecutorDevelopment() {
    var threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(8);
    threadPoolTaskExecutor.setMaxPoolSize(16);
    return threadPoolTaskExecutor;
  }

  @Profile({Profiles.TEST})
  @Bean
  @Primary
  public Executor testThreadPoolTaskExecutor() {
    return Executors.newSingleThreadExecutor();
  }

  @Profile({Profiles.PRODUCTION, Profiles.DEVELOPMENT})
  @Bean
  public Clock prodClock() {
    return Clock.systemDefaultZone();
  }

  @Profile(Profiles.TEST)
  @Bean
  public Clock testClock() {
    return Clock.fixed(Instant.parse("2000-01-01T10:00:55.000Z"), ZoneId.systemDefault());
  }
}

