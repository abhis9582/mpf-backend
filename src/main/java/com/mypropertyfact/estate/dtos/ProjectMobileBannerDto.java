package com.mypropertyfact.estate.dtos;

import lombok.Data;

@Data
public class ProjectMobileBannerDto {
    private long id;
    private int projectId;
    private String projectName;
    private String slugURL;
    private String mobileAltTag;
    private String mobileImage;
}
