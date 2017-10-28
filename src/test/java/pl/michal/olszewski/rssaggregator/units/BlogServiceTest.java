package pl.michal.olszewski.rssaggregator.units;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.michal.olszewski.rssaggregator.service.BlogService;

public class BlogServiceTest {

    private BlogService blogService;

    @Before
    public void setUp() throws Exception {
        blogService=new BlogService();
    }

    @Test
    public void shouldCreateBlogFromURL(){
        Assert.fail();
    }

    @Test
    public void shouldNotCreateBlogWhenThrowException(){
        Assert.fail();
    }

    @Test
    public void shouldUpdateBlogWhenNewItemAdd(){
        Assert.fail();
    }

    @Test
    public void shouldNotUpdateBlogWhenNothingChanged(){
        Assert.fail();
    }

    @Test
    public void shouldUpdateBlogWhenDescriptionChanged(){
        Assert.fail();
    }

    @Test
    public void shouldGetBlogByName(){
        Assert.fail();
    }

    @Test
    public void shouldThrownExceptionWhenBlogByNameNotExist(){
        Assert.fail();
    }

    @Test
    public void shouldGetBlogById(){
        Assert.fail();
    }

    @Test
    public void shouldThrownExceptionWhenBlogByIdNotExist(){
        Assert.fail();
    }
}
