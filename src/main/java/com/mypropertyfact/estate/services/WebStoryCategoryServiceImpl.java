package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.WebStoryCategoryDto;
import com.mypropertyfact.estate.configs.dtos.WebStoryDto;
import com.mypropertyfact.estate.entities.WebStoryCategory;
import com.mypropertyfact.estate.interfaces.WebStoryCategoryService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.WebStoryCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class WebStoryCategoryServiceImpl implements WebStoryCategoryService {

    @Autowired
    private WebStoryCategoryRepository webStoryCategoryRepository;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public Response addUpdate(WebStoryCategoryDto webStoryCategoryDto) {
        Response response = new Response();

        String slug = fileUtils.generateSlug(webStoryCategoryDto.getCategoryName());

        // Check for duplicate category name
        Optional<WebStoryCategory> existingCategory = webStoryCategoryRepository.findByCategoryName(slug);

        if (webStoryCategoryDto.getId() > 0) {
            Optional<WebStoryCategory> savedCategory = webStoryCategoryRepository.findById(webStoryCategoryDto.getId());

            if (savedCategory.isPresent()) {
                WebStoryCategory category = savedCategory.get();

                // Prevent updating to a name that already exists in another record
                if (existingCategory.isPresent() && !(existingCategory.get().getId() == category.getId())) {
                    response.setIsSuccess(0);
                    response.setMessage("Category name already exists.");
                    return response;
                }

                category.setCategoryName(slug);
                category.setCategoryDescription(webStoryCategoryDto.getCategoryDescription());
                webStoryCategoryRepository.save(category);
                response.setIsSuccess(1);
                response.setMessage("Web story category updated successfully...");
            } else {
                response.setIsSuccess(0);
                response.setMessage("Web story category not found.");
            }

        } else {
            // New insert - check if name already exists
            if (existingCategory.isPresent()) {
                response.setIsSuccess(0);
                response.setMessage("Category name already exists.");
                return response;
            }

            WebStoryCategory webStoryCategory = new WebStoryCategory();
            webStoryCategory.setCategoryName(slug);
            webStoryCategory.setCategoryDescription(webStoryCategoryDto.getCategoryDescription());
            webStoryCategoryRepository.save(webStoryCategory);
            response.setIsSuccess(1);
            response.setMessage("Web story category saved successfully...");
        }

        return response;
    }


    @Override
    @Transactional
    public List<WebStoryCategoryDto> getAllCategories() {
        List<WebStoryCategory> allCategories = webStoryCategoryRepository.findAll();
        return allCategories.stream().map(category -> {
            WebStoryCategoryDto webStoryCategoryDto = new WebStoryCategoryDto();
            webStoryCategoryDto.setId(category.getId());
            webStoryCategoryDto.setCategoryName(category.getCategoryName());
            webStoryCategoryDto.setCategoryDescription(category.getCategoryDescription());
            if(category.getWebStories() != null && !category.getWebStories().isEmpty()) {
                webStoryCategoryDto.setStoryCategoryImage(category.getWebStories().get(0).getStoryImage());
            }
            List<WebStoryDto> webStoryDtoList = category.getWebStories().stream().map(webStory -> {
                WebStoryDto webStoryDto = new WebStoryDto();
                webStoryDto.setCategoryId(webStory.getWebStoryCategory().getId());
                webStoryDto.setStoryImage(webStory.getStoryImage());
                webStoryDto.setStoryTitle(webStoryDto.getStoryTitle());
                webStoryDto.setStoryDescription(webStory.getStoryDescription());
                webStoryDto.setId(webStory.getId());
                return webStoryDto;
            }).toList();
            webStoryCategoryDto.setWebStories(webStoryDtoList);
            return webStoryCategoryDto;
        }).toList();
    }

    @Override
    public void deleteCategory(int categoryId) {
        webStoryCategoryRepository.deleteById(categoryId);
    }
}
