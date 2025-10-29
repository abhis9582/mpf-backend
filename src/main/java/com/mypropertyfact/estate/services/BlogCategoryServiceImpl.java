package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.BlogCategoryDto;
import com.mypropertyfact.estate.entities.BlogCategory;
import com.mypropertyfact.estate.interfaces.BlogCategoryService;
import com.mypropertyfact.estate.models.ResourceNotFoundException;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.BlogCategoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogCategoryServiceImpl implements BlogCategoryService {

    private final BlogCategoryRepository blogCategoryRepository;

    @Value("${upload_dir}")
    private String uploadDir;

    private final FileUtils fileUtils;

    BlogCategoryServiceImpl(BlogCategoryRepository blogCategoryRepository,
                            FileUtils fileUtils) {
        this.blogCategoryRepository = blogCategoryRepository;
        this.fileUtils = fileUtils;
    }

    @Override
    public Response addUpdateBlogCategory(BlogCategory blogCategory) {
        if (blogCategory == null) {
            throw new IllegalArgumentException("Blog category cannot be null");
        }
        if (blogCategory.getId() > 0) {
            BlogCategory dbBlogCategory = blogCategoryRepository.findById(blogCategory.getId()).orElseThrow(() -> new ResourceNotFoundException("Blog category not found with id " + blogCategory.getId()));
            dbBlogCategory.setCategoryName(blogCategory.getCategoryName());
            dbBlogCategory.setCategoryDescription(blogCategory.getCategoryDescription());
            blogCategoryRepository.save(dbBlogCategory);
            return new Response(1, "Blog category updated successfully...", 0);
        }
        blogCategoryRepository.save(blogCategory);
        return new Response(1, "Blog category saved successfully...", 0);
    }

    @Override
    @Transactional
    public Response deleteBlogCategory(int id) {
        BlogCategory blogCategory = blogCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog category not found or already deleted"));
        if (blogCategory.getBlogs() != null) {
            blogCategory.getBlogs().forEach(blog -> {
                String imageName = blog.getBlogImage();
                if (imageName != null && !imageName.isEmpty()) {
                    fileUtils.deleteFileFromDestination(imageName, uploadDir + "blog/");
                }
            });
        }
        blogCategoryRepository.delete(blogCategory);
        return new Response(1, "Blog category deleted successfully", 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogCategoryDto> getAllCategories() {
        List<BlogCategory> categories = blogCategoryRepository.findAllWithBlogs();

        return categories.stream().map(
                blogCategory -> new BlogCategoryDto(
                        blogCategory.getId(),
                        blogCategory.getCategoryName(),
                        blogCategory.getCategoryDescription(),
                        blogCategory.getBlogs().size()
                )
        ).collect(Collectors.toList());
    }

    @Override
    public Optional<BlogCategory> getBlogCategoryById(int id) {
        return blogCategoryRepository.findById(id);
    }
}
