package com.mypropertyfact.estate.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ProjectTypeDto {
    private int id;
    private String metaTitle;
    private String metaKeywords;
    private String metaDescription;
    private String projectTypeName;
    private String projectTypeDescription;
    private String slugURL;
    private List<ProjectShortDetails> projectList;
}
