package com.mypropertyfact.estate.configs.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Data
public class ProjectBannerDto {
    private MultipartFile desktopBanner;
    private MultipartFile mobileBanner;
    private int projectId;
    private String type;
    private String projectName;
}
