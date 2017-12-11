package pl.michal.olszewski.rssaggregator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("refresh.blog")
@Data
public class RefreshBlogProperties {

  private Integer milis = 5 * 1000 * 60;
  private boolean enableJob = false;
}
