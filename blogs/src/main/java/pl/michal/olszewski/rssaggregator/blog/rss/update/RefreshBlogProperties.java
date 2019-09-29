package pl.michal.olszewski.rssaggregator.blog.rss.update;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("refresh.blog")
class RefreshBlogProperties {

  private Integer milis = 5 * 1000 * 60;

  Integer getMilis() {
    return milis;
  }

  void setMilis(Integer milis) {
    this.milis = milis;
  }
}
