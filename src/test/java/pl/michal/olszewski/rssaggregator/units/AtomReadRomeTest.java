package pl.michal.olszewski.rssaggregator.units;

import com.rometools.rome.io.XmlReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.service.RssExtractorService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AtomReadRomeTest {

    private RssExtractorService rssExtractorService;


    @Before
    public void setUp() {
        rssExtractorService = new RssExtractorService();
    }
    @Test
    public void shouldCorrectTransformXmlToRome(){
        try {
            BlogDTO blog = rssExtractorService.getBlog(new XmlReader(new File("AtomExample.xml")), "feedURL");
            assertThat(blog).isNotNull();
            assertThat(blog.getFeedURL()).isEqualTo("feedURL");
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldTransformWithCorrectTitle(){
        try {
            BlogDTO blog = rssExtractorService.getBlog(new XmlReader(new File("AtomExample.xml")), "feedURL");
            assertThat(blog.getName()).isNotNull().isEqualTo("Test RSS");
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldTransformWithCorrectDescriptionAndPublicationDate(){
        try {
            BlogDTO blog = rssExtractorService.getBlog(new XmlReader(new File("AtomExample.xml")), "feedURL");
            assertThat(blog.getPublishedDate()).isNotNull().isEqualTo(LocalDateTime.of(2017, 10, 27, 14, 9).atZone(ZoneId.of("UTC")).toInstant());
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldTransformWithCorrectBlogItems(){
        try {
            BlogDTO blog = rssExtractorService.getBlog(new XmlReader(new File("AtomExample.xml")), "feedURL");
            List<ItemDTO> itemsForBlog = blog.getItemsList();
            assertThat(itemsForBlog).isEmpty();
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldThrowException(){
        assertThatThrownBy(()->rssExtractorService.getBlog(null, "test")).isNotNull().hasMessage("Wystąpił błąd przy pobieraniu informacji z bloga test");
    }

}
