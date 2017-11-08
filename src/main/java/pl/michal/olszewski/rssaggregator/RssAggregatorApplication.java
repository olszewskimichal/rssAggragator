package pl.michal.olszewski.rssaggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RssAggregatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(RssAggregatorApplication.class, args);
  }
}
