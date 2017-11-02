package pl.michal.olszewski.rssaggregator.factory;

import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlogListFactory {

    private final BlogRepository repository;

    public BlogListFactory(BlogRepository repository) {
        this.repository = repository;
    }

    public List<BlogDTO> buildNumberOfBlogsDTOAndSave(int numberOfBlogs) {
        IntStream.range(0, numberOfBlogs).mapToObj(number -> new Blog("blog" + number, "", "", "", null)).
                forEach(repository::save);
        return repository.findAll().stream().map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), Collections.emptyList()))
                .collect(Collectors.toList());
    }

    public static List<BlogDTO> getNotPersistedBlogs(int numberOfBlogs) {
        return IntStream.range(0, numberOfBlogs).mapToObj(number -> new Blog("blog" + number, "", "", "", null))
                .map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), Collections.emptyList()))
                .collect(Collectors.toList());
    }

    public List<Blog> buildNumberOfBlogsAndSave(int numberOfBlogs) {
        IntStream.range(0, numberOfBlogs).mapToObj(number -> new Blog("blog" + number, "", "", "", null)).
                forEach(repository::save);
        return repository.findAll();
    }
}
