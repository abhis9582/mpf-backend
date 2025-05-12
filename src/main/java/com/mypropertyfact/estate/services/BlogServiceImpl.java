package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.BlogDto;
import com.mypropertyfact.estate.entities.Blog;
import com.mypropertyfact.estate.interfaces.BlogService;
import com.mypropertyfact.estate.models.ResourceNotFoundException;
import com.mypropertyfact.estate.models.Response;
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

    @Value("${upload_dir}")
    private String upload_dir;
    @Autowired
    private FileUtils fileUtils;


    @Override
    public Response addUpdateBlog(MultipartFile blogImage, Blog blog) {
        // Generate slug first (to use in filename and DB)
        String generatedSlug = fileUtils.generateSlug(blog.getSlugUrl());
        blog.setSlugUrl(generatedSlug);

        String blogImageName = null;

        if (blogImage != null && !blogImage.isEmpty()) {
            // Validate file
            if (!fileUtils.isFileSizeValid(blogImage, 5 * 1024 * 1024)) {
                throw new IllegalArgumentException("File size exceeds the 2MB limit.");
            }
            if (!fileUtils.isTypeImage(blogImage)) {
                throw new IllegalArgumentException("Invalid file type.");
            }

            // Rename and save image
            String  imageName = fileUtils.renameFile(blogImage, blog.getSlugUrl());
            String dir = Paths.get(upload_dir, "blog").toString();
            blogImageName = fileUtils.saveFile(blogImage, imageName, dir, 1200, 628, 0.9f); // Save resized and converted
        }

        // Update blog
        if (blog.getId() > 0) {
            Blog existing = blogRepository.findById(blog.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found"));

            // Delete old image only if new one is uploaded
            if (blogImageName != null && existing.getBlogImage() != null) {
                fileUtils.deleteFileFromDestination(existing.getBlogImage(), upload_dir+"blog/");
            }

            // Update fields
            existing.setBlogTitle(blog.getBlogTitle());
            existing.setBlogDescription(blog.getBlogDescription());
            existing.setSlugUrl(blog.getSlugUrl());
            existing.setStatus(blog.getStatus());
            existing.setBlogKeywords(blog.getBlogKeywords());
            existing.setBlogCategory(blog.getBlogCategory());
            existing.setBlogMetaDescription(blog.getBlogMetaDescription());
            if (blogImageName != null) {
                existing.setBlogImage(blogImageName);
            }
            blogRepository.save(existing);
            return new Response(1, "Blog updated successfully...");
        }

        // Add new blog
        if (blogRepository.existsBySlugUrl(blog.getSlugUrl())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug URL already exists");
        }

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
        return blogRepository.findAll().stream().map(blog ->
            new BlogDto(blog.getId(),
                    blog.getBlogTitle(),
                    blog.getBlogKeywords(),
                    blog.getBlogMetaDescription(),
                    blog.getBlogDescription(),
                    blog.getSlugUrl(),
                    blog.getBlogImage(),
                    blog.getBlogCategory().getCategoryName(),
                    blog.getBlogCategory().getId()
            )
        ).collect(Collectors.toList());
    }

    public BlogDto getBySlug(String slug){
        Blog bySlugUrl = blogRepository.findBySlugUrl(slug);
        return new BlogDto(bySlugUrl.getId(),
                bySlugUrl.getBlogTitle(),
                bySlugUrl.getBlogKeywords(),
                bySlugUrl.getBlogMetaDescription(),
                bySlugUrl.getBlogDescription(),
                bySlugUrl.getSlugUrl(),
                bySlugUrl.getBlogImage(),
                bySlugUrl.getBlogCategory().getCategoryName(),
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
    public Response deleteBlog(int id){
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Blog already deleted or not found"));
        fileUtils.deleteFileFromDestination(blog.getBlogImage(), upload_dir+"blog/");
        blogRepository.delete(blog);
        return new Response(1, "Blog deleted successful...");
    }
}
