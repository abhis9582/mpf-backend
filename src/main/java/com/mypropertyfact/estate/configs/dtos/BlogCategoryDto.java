package com.mypropertyfact.estate.configs.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogCategoryDto {
    private int id;
    private String categoryName;
    private String categoryDescription;
}
