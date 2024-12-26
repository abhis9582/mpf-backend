package com.mypropertyfact.estate.configs.dtos;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PropertyDetailDto {
    private int id;
    private String metaTitle;
    private String metaKeyword;
    private String metaDescription;
    private String projectName;
    private String projectAddress;//
    private String state;
    private String cityLocation;
    private String projectLocality;
    private String projectConfiguration;
    private String projectBy;
    private String projectPrice;
    private long ivrNo;
    private MultipartFile locationMap;
    private String reraNo;
    private String reraQr;
    private String reraWebsite;
    private String cityPriority;
    private String luxuryPriority;
    private String newLaunchPriority;
    private String featuredPriority;
    private String recentPriority;
    private String residentialPriority;
    private String commercialPriority;
    private String projectStatus;
    private MultipartFile projectLogo;
    private MultipartFile projectThumbnail;
    private String propertyType;
    private String slugURL;
    private String showSimilarProperties;
    private String status;
    private String showFeaturedProperties;
    private String country;
    private String aboutDesc;
    private String walkthroughDesc;
    private String floorPlanDesc;
    private String amenityDesc;
    private String locationDesc;
    private List<Integer> amenities;
}
