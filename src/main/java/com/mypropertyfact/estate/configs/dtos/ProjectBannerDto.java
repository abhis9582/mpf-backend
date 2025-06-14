package com.mypropertyfact.estate.configs.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Data
public class ProjectBannerDto {
    private int id;
    private MultipartFile desktopBanner;
    private MultipartFile mobileBanner;
    private int projectId;
    private String altTag;
    private String projectName;
    private String slugURL;
    private String desktopImage;
    private String mobileImage;
}
