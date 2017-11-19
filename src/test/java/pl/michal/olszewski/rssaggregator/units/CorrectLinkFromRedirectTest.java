package pl.michal.olszewski.rssaggregator.units;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.michal.olszewski.rssaggregator.service.RssExtractorService.convertURLToAscii;
import static pl.michal.olszewski.rssaggregator.service.RssExtractorService.getFinalURL;

import org.junit.jupiter.api.Test;

class CorrectLinkFromRedirectTest {

  @Test
  void shouldConvertUrlToUrlWithAscii() {
    String val = "http://jvm-bloggers.com/r/ṡ𢋭猹𡃹𤚴󠄍㯱";
    String val2 = "http://jvm-bloggers.com/r/%E1%B9%A1%F0%A2%8B%AD%E7%8C%B9%F0%A1%83%B9%F0%A4%9A%B4%F3%A0%84%8D%E3%AF%B1";
    assertThat(convertURLToAscii(val)).isEqualTo(val2);

    val = "http://jvm-bloggers.com/r/𝖑𠘘㶖𤏼🄹≝摩";
    val2 = "http://jvm-bloggers.com/r/%F0%9D%96%91%F0%A0%98%98%E3%B6%96%F0%A4%8F%BC%F0%9F%84%B9%E2%89%9D%F0%AF%A3%83";
    assertThat(convertURLToAscii(val)).isEqualTo(val2);

    val = "https://kobietydokodu.pl/rozmowa-o-prace-w-it%e2%80%8a-%e2%80%8ao-co-warto-zapytac-rekrutera/";
    assertThat(convertURLToAscii(val)).isEqualTo(val);
  }

  @Test
  void shouldGetFinalLinkFromRedirect() {
    assertThat(getFinalURL("http://jvm-bloggers.com/r/%E1%B9%A1%F0%A2%8B%AD%E7%8C%B9%F0%A1%83%B9%F0%A4%9A%B4%F3%A0%84%8D%E3%AF%B1"))
        .isEqualTo("http://www.samouczekprogramisty.pl/modyfikatory-dostepu-w-jezyku-java/?utm_source=jvm-bloggers.com&utm_medium=link&utm_campaign=jvm-bloggers");
  }

}
