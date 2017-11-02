package pl.michal.olszewski.rssaggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.exception.BlogNotFoundException;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
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

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public boolean deleteBlog(Long id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new BlogNotFoundException(id));
        blogRepository.delete(blog);
        return true;
    }

    public BlogDTO getBlogDTOById(Long id) {
        Blog blogById = getBlogById(id);
        return new BlogDTO(blogById.getBlogURL(), blogById.getDescription(), blogById.getName(), blogById.getFeedURL(), blogById.getPublishedDate(), blogById.getItems().stream().map(v -> new ItemDTO(v.getTitle(), v.getDescription(), v.getLink(), v.getDate(), v.getAuthor())).collect(Collectors.toList()));
    }

    public List<BlogDTO> getAllBlogDTOs(Integer limit, Integer page) {
        PageRequest pageRequest = new PageRequest(getPage(page), getLimit(limit));
        return blogRepository.findAll(pageRequest).getContent().stream().map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), new ArrayList<>())).collect(Collectors.toList());
    }

    private int getLimit(final Integer size) {
        return (Objects.isNull(size) ? 20 : size);
    }

    private int getPage(final Integer page) {
        return (Objects.isNull(page) ? 0 : page);
    }
}
