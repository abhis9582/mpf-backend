package com.mypropertyfact.estate.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
public class ProjectBannerDto {
    private int id;
    private int projectId;
    private String projectName;
    private String slugURL;
    private String altTag;
    private List<MultipartFile> projectDesktopBannerImageList;
    private List<MultipartFile> projectMobileBannerImageList;
    private String projectMobileBanner;
    private String projectDesktopBanner;
    private List<Integer> deletedMobileImageIds;
    private List<Integer> deletedDesktopImageIds;
}
