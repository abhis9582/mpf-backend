package com.mypropertyfact.estate.dtos;

import lombok.Data;

import java.util.List;

@Data
public class BuilderDto {
    private int id;
    private String metaTitle;
    private String metaKeywords;
    private String metaDescription;
    private String builderName;
    private String builderDescription;
    private String slugURL;
    private List<ProjectDetailDto> projectList;
}
