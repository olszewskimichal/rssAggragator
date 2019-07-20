package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HtmlTagRemoverTest {

  @Test
  void shouldRemoveHtmlTagsFromDescription() {
    //given
    String description = "<p>W poprzednim artykule opisałem, jak testować proste odpowiedzi z API. Zazwyczaj API zwraca rozbudowane obiekty. Dlatego rozpatrzmy przypadek, w którym nasze API będzie zwracało tablice obiektów typu Person. Kod API jak poniżej: Testowanie ilości zwracanych elementów API zwraca określoną liczbę elementów, w zależności od wartości przekazanej do Path. Toteż dobrze będzie wykonać test, który sprawdzi, [&#8230;]</p> <p>Artykuł <a rel=\"nofollow\" href=\"http://bykowski.pl/spring-boot-26-testy-dla-zwracanych-obiektow-z-api/\">Spring Boot #26 – Testy dla zwracanych obiektów z API</a> pochodzi z serwisu <a rel=\"nofollow\" href=\"http://bykowski.pl\">Przemysław Bykowski</a>.</p>";

    //when
    String result = HtmlTagRemover.removeHtmlTagFromDescription(description);

    //then
    assertThat(result).isEqualTo(
        "W poprzednim artykule opisałem, jak testować proste odpowiedzi z API. Zazwyczaj API zwraca rozbudowane obiekty. Dlatego rozpatrzmy przypadek, w którym nasze API będzie zwracało tablice obiektów typu Person. Kod API jak poniżej: Testowanie ilości zwracanych elementów API zwraca określoną liczbę elementów, w zależności od wartości przekazanej do Path. Toteż dobrze będzie wykonać test, który sprawdzi, […] Artykuł Spring Boot #26 – Testy dla zwracanych obiektów z API pochodzi z serwisu Przemysław Bykowski.");
  }
}