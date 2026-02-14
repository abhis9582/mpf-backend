package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.WebStoryCategoryDto;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface WebStoryCategoryService {
    Response addUpdate(WebStoryCategoryDto webStoryCategoryDto);
    List<WebStoryCategoryDto> getAllCategories();
    Response deleteCategory(int categoryId);
}
