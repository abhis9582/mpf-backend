package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.BlogCategoryDto;
import com.mypropertyfact.estate.entities.BlogCategory;
import com.mypropertyfact.estate.models.Response;

import java.util.List;
import java.util.Optional;

public interface BlogCategoryService {
    Response addUpdateBlogCategory(BlogCategory blogCategory);
    Response deleteBlogCategory(int id);
    List<BlogCategoryDto> getAllCategories();
    Optional<BlogCategory> getBlogCategoryById(int id);
}
