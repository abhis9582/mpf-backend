package com.mypropertyfact.estate.dtos;

import lombok.Data;

@Data
public class WebStoryDto {

    private int id;
    private int categoryId;
    private String categoryName;
    private String storyImage;
    private String storyTitle;
    private String storyDescription;
}
