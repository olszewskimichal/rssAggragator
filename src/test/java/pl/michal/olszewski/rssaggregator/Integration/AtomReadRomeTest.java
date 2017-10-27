package pl.michal.olszewski.rssaggregator.Integration;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.junit.Assert;
import org.junit.Test;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.service.RssExtractorService;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AtomReadRomeTest {

    @Test
    public void shouldCorrectTransformXmlToRome(){
        try(XmlReader reader=new XmlReader(new File("AtomExample.xml"))){
            SyndFeed feed = new SyndFeedInput().build(reader);
            assertThat(feed).isNotNull();
        } catch (IOException | FeedException e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldTransformWithCorrectTitle(){
        try(XmlReader reader=new XmlReader(new File("AtomExample.xml"))){
            SyndFeed feed = new SyndFeedInput().build(reader);
            assertThat(feed.getTitle()).isNotNull().isEqualTo("Test RSS");
        } catch (IOException | FeedException e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldTransformWithCorrectDescriptionAndPublicationDate(){
        try(XmlReader reader=new XmlReader(new File("AtomExample.xml"))){
            SyndFeed feed = new SyndFeedInput().build(reader);
            assertThat(feed.getPublishedDate()).isNotNull().isEqualTo(Date.from(LocalDateTime.of(2017,10,27,14,9).atZone(ZoneId.of("UTC")).toInstant()));
        } catch (IOException | FeedException e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldTransformWithCorrectBlogItems(){
        try(XmlReader reader=new XmlReader(new File("AtomExample.xml"))){
            SyndFeed feed = new SyndFeedInput().build(reader);
            List<ItemDTO> itemsForBlog = RssExtractorService.getItemsForBlog(feed);
            assertThat(itemsForBlog).isNotEmpty().hasSize(1);
            assertThat(itemsForBlog.get(0).getTitle()).isEqualTo("testowy item");
            assertThat(itemsForBlog.get(0).getLink()).isEqualTo("https://item.pl");
            assertThat(itemsForBlog.get(0).getDescription()).isEqualTo("Opis");
            assertThat(itemsForBlog.get(0).getAuthor()).isEqualTo("Michał");
            assertThat(itemsForBlog.get(0).getDate().atZone(ZoneId.systemDefault())).isEqualTo(LocalDateTime.of(2017,10,27,14,9).atZone(ZoneId.of("UTC")));
        } catch (IOException | FeedException e) {
            Assert.fail();
        }
    }

}
