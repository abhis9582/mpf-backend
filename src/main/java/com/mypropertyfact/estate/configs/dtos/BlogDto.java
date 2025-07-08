package com.mypropertyfact.estate.configs.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogDto {
    private int id;
    private String blogTitle;
    private String blogKeywords;
    private String blogMetaDescription;
    private String blogDescription;
    private String slugUrl;
    private String blogImage;
    private String blogCategory;
    private int status;
    private int categoryId;
}
