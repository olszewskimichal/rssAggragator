package pl.michal.olszewski.rssaggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.exception.BlogNotFoundException;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

import java.util.List;

@Service
@Slf4j
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public Blog createBlog(BlogDTO blogDTO) {
        Blog blog = new Blog(blogDTO.getLink(), blogDTO.getDescription(), blogDTO.getName(), blogDTO.getFeedURL(), blogDTO.getPublishedDate());
        blogDTO.getItemsList().stream().map(Item::new).forEach(blog::addItem);
        blogRepository.save(blog);
        return blog;
    }

    public Blog getBlogByURL(String url) {
        return blogRepository.findByBlogURL(url).orElseThrow(() -> new BlogNotFoundException(url));
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id).orElseThrow(() -> new BlogNotFoundException(id));
    }

    public Blog updateBlog(BlogDTO blogDTO) {
        log.debug(blogDTO.toString());
        Blog blog = getBlogByURL(blogDTO.getLink());
        blog.updateFromDto(blogDTO);
        blogDTO.getItemsList().stream().map(Item::new).forEach(blog::addItem);
        blogRepository.save(blog);
        return blog;
    }

    public List<Blog> getAllBlogs(){
        return blogRepository.findAll();
    }
}
