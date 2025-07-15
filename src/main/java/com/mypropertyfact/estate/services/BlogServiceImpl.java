package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.BlogDto;
import com.mypropertyfact.estate.entities.Blog;
import com.mypropertyfact.estate.entities.BlogCategory;
import com.mypropertyfact.estate.interfaces.BlogService;
import com.mypropertyfact.estate.models.ResourceNotFoundException;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.BlogCategoryRepository;
import com.mypropertyfact.estate.repositories.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BlogCategoryRepository blogCategoryRepository;

    @Value("${upload_dir}")
    private String upload_dir;
    @Autowired
    private FileUtils fileUtils;


    @Override
    public Response addUpdateBlog(MultipartFile blogImage, BlogDto blogDto) {
        // Generate slug first
        String generatedSlug = fileUtils.generateSlug(blogDto.getSlugUrl());
        blogDto.setSlugUrl(generatedSlug);
        Optional<BlogCategory> blogCategory = blogCategoryRepository.findById(Integer.parseInt(blogDto.getBlogCategory()));
        String blogImageName = null;
        Blog existing = blogDto.getId() > 0 ? blogRepository.findById(blogDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found")) : new Blog();;
        if (blogImage != null && !blogImage.isEmpty()) {
            // Validate file
            if (!fileUtils.isFileSizeValid(blogImage, 5 * 1024 * 1024)) {
                throw new IllegalArgumentException("File size exceeds the 2MB limit.");
            }
            if (!fileUtils.isTypeImage(blogImage)) {
                throw new IllegalArgumentException("Invalid file type.");
            }
            // Delete old image BEFORE saving new one
            if (blogDto.getId() > 0) {
                existing = blogRepository.findById(blogDto.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found"));

                if (existing.getBlogImage() != null && !existing.getBlogImage().isBlank()) {
                    fileUtils.deleteFileFromDestination(existing.getBlogImage(), upload_dir + "blogDto/");
                }
            }
            // Rename and save image
            String imageName = fileUtils.renameFile(blogImage, blogDto.getSlugUrl());
            String dir = Paths.get(upload_dir, "blog/").toString();
            blogImageName = fileUtils.saveFile(blogImage, imageName, dir, 1200, 628, 0.9f); // Save resized and converted
        }

        // Update blogDto
        if (blogDto.getId() > 0) {
            // Delete old image only if new one is uploaded
            if (blogImageName != null && existing.getBlogImage() != null) {
                fileUtils.deleteFileFromDestination(existing.getBlogImage(), upload_dir + "blogDto/");
            }

            // Update fields
            existing.setBlogTitle(blogDto.getBlogTitle());
            existing.setBlogDescription(blogDto.getBlogDescription());
            if(!existing.getSlugUrl().equals(blogDto.getSlugUrl())){
                existing.setSlugUrl(blogDto.getSlugUrl());
            }
            existing.setStatus(blogDto.getStatus());
            existing.setBlogKeywords(blogDto.getBlogKeywords());
            blogCategory.ifPresent(existing::setBlogCategory);
            existing.setBlogMetaDescription(blogDto.getBlogMetaDescription());
            if (blogImageName != null) {
                existing.setBlogImage(blogImageName);
            }
            blogRepository.save(existing);
            return new Response(1, "Blog updated successfully...");
        }

        // Add new blogDto
        if (blogRepository.existsBySlugUrl(blogDto.getSlugUrl())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug URL already exists");
        }
        Blog blog = new Blog();
        blogCategory.ifPresent(blog::setBlogCategory);
        blog.setBlogTitle(blogDto.getBlogTitle());
        blog.setBlogDescription(blogDto.getBlogDescription());
        blog.setSlugUrl(blogDto.getSlugUrl());
        blog.setStatus(blogDto.getStatus());
        blog.setBlogKeywords(blogDto.getBlogKeywords());
        blog.setBlogMetaDescription(blogDto.getBlogMetaDescription());
        blog.setBlogImage(blogImageName);
        blogRepository.save(blog);
        return new Response(1, "Blog saved successfully...");
    }

    @Override
    public Optional<Blog> getBlogById(int id) {
        if (!blogRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found");
        }
        return blogRepository.findById(id);
    }

    @Override
    public List<BlogDto> getAllBlogs() {
        List<Blog> blogs = blogRepository.findAll();
        return blogs.stream().map(blog ->
                new BlogDto(blog.getId(),
                        blog.getBlogTitle(),
                        blog.getBlogKeywords(),
                        blog.getBlogMetaDescription(),
                        blog.getBlogDescription(),
                        blog.getSlugUrl(),
                        blog.getBlogImage(),
                        blog.getBlogCategory().getCategoryName(),
                        blog.getStatus(),
                        blog.getBlogCategory().getId()
                )
        ).collect(Collectors.toList());
    }

    public BlogDto getBySlug(String slug) {
        Blog bySlugUrl = blogRepository.findBySlugUrl(slug);
        return new BlogDto(bySlugUrl.getId(),
                bySlugUrl.getBlogTitle(),
                bySlugUrl.getBlogKeywords(),
                bySlugUrl.getBlogMetaDescription(),
                bySlugUrl.getBlogDescription(),
                bySlugUrl.getSlugUrl(),
                bySlugUrl.getBlogImage(),
                bySlugUrl.getBlogCategory().getCategoryName(),
                bySlugUrl.getStatus(),
                bySlugUrl.getBlogCategory().getId()
        );
    }

    @Override
    public Page<Blog> getWithPagination(int page, int size) {
        int p = page;
        int s = size;
        Page<Blog> all = blogRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return all;
    }

    @Override
    public Response deleteBlog(int id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Blog already deleted or not found"));
        fileUtils.deleteFileFromDestination(blog.getBlogImage(), upload_dir + "blog/");
        blogRepository.delete(blog);
        return new Response(1, "Blog deleted successful...");
    }
}
