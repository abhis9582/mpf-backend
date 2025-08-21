package com.mypropertyfact.estate.dtos;

import lombok.Data;

@Data
public class ProjectDesktopBannerDto {
    private long id;
    private int projectId;
    private String projectName;
    private String slugURL;
    private String desktopImage;
    private String desktopAltTag;
}
