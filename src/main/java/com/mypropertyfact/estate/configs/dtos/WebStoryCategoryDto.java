package com.mypropertyfact.estate.configs.dtos;

import com.mypropertyfact.estate.entities.WebStory;
import lombok.Data;

import java.util.List;

@Data
public class WebStoryCategoryDto {
    private int id;
    private String categoryName;
    private String categoryDescription;
    private List<WebStoryDto> webStories;
}
