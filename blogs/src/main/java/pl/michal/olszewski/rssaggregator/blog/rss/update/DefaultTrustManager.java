package pl.michal.olszewski.rssaggregator.blog.rss.update;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

class DefaultTrustManager implements X509TrustManager {

  @Override
  public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
  }

  @Override
  public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[]{};
  }

}