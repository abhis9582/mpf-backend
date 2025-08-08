package com.mypropertyfact.estate.configs.dtos;

import com.mypropertyfact.estate.entities.Blog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogCategoryDto {
    private int id;
    private String categoryName;
    private String categoryDescription;
//    private List<Blog> blogs;
    private int noOfBlogs;
}
