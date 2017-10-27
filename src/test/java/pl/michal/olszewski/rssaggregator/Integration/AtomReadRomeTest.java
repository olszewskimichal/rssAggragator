package pl.michal.olszewski.rssaggregator.Integration;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
            assertThat(feed.getPublishedDate()).isNotNull().isEqualTo(Date.from(LocalDateTime.of(2017,10,27,16,9).atZone(ZoneId.systemDefault()).toInstant()));
        } catch (IOException | FeedException e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldTransformWithCorrectBlogItems(){
        try(XmlReader reader=new XmlReader(new File("AtomExample.xml"))){
            SyndFeed feed = new SyndFeedInput().build(reader);
            assertThat(feed.getEntries()).isNotEmpty().hasSize(1);
            assertThat(feed.getEntries().get(0).getTitle()).isEqualTo("testowy item");
            assertThat(feed.getEntries().get(0).getLink()).isEqualTo("https://item.pl");
            assertThat(feed.getEntries().get(0).getDescription().getValue()).isEqualTo("Opis");
            assertThat(feed.getEntries().get(0).getAuthor()).isEqualTo("Michał");
            assertThat(feed.getEntries().get(0).getPublishedDate()).isEqualTo(Date.from(LocalDateTime.of(2017,10,27,16,9).atZone(ZoneId.systemDefault()).toInstant()));
        } catch (IOException | FeedException e) {
            Assert.fail();
        }
    }

}
