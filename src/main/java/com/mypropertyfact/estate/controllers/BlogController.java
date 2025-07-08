package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.BlogDto;
import com.mypropertyfact.estate.entities.Blog;
import com.mypropertyfact.estate.interfaces.BlogService;
import com.mypropertyfact.estate.models.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdate(@RequestParam(required = false) MultipartFile image, @Valid @ModelAttribute BlogDto blogDto) {
        return new ResponseEntity<>(blogService.addUpdateBlog(image, blogDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getOne(@PathVariable int id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found"));
    }

    @GetMapping("/get-all")
    public List<BlogDto> getAll() {
        return blogService.getAllBlogs();
    }

    @GetMapping("/get/{slug}")
    public ResponseEntity<BlogDto> getAll(@PathVariable String slug) {
        return ResponseEntity.ok(blogService.getBySlug(slug));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteBlog(@PathVariable int id){
        return ResponseEntity.ok(blogService.deleteBlog(id));
    }

    @GetMapping("/get")
    public Page<Blog> getWithPagination(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "9")int size
    ){
        return blogService.getWithPagination(page, size);
    }
}

