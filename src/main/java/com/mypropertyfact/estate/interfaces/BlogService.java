package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.configs.dtos.BlogDto;
import com.mypropertyfact.estate.entities.Blog;
import com.mypropertyfact.estate.models.Response;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BlogService {
    Response addUpdateBlog(MultipartFile blogImage, BlogDto blogDto);
    Response deleteBlog(int id);
    Optional<Blog> getBlogById(int id);
    List<BlogDto> getAllBlogs();

    BlogDto getBySlug(String slug);

    Page<Blog> getWithPagination(int page, int size);
}
