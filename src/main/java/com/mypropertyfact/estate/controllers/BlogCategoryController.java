package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.BlogCategoryDto;
import com.mypropertyfact.estate.entities.BlogCategory;
import com.mypropertyfact.estate.interfaces.BlogCategoryService;
import com.mypropertyfact.estate.models.ResourceNotFoundException;
import com.mypropertyfact.estate.models.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog-category")
public class BlogCategoryController {
    private BlogCategoryService blogCategoryService;
    
    public BlogCategoryController(BlogCategoryService blogCategoryService){
        this.blogCategoryService = blogCategoryService;
    }
    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdateBlogCategory(@RequestBody BlogCategory blogCategory){
        return ResponseEntity.ok(blogCategoryService.addUpdateBlogCategory(blogCategory));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<BlogCategoryDto>> getAll(){
        return ResponseEntity.ok(blogCategoryService.getAllCategories());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<BlogCategory> getBlogCategoryById(@PathVariable("id") int id){
        return ResponseEntity.ok(blogCategoryService.getBlogCategoryById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Blog category not found with this category id: "+ id)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteBlogCategory(@PathVariable("id")int id){
        return ResponseEntity.ok(blogCategoryService.deleteBlogCategory(id));
    }
}
