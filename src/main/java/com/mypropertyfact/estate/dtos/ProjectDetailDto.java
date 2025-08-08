package com.mypropertyfact.estate.dtos;

import com.mypropertyfact.estate.models.AmenityDto;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDetailDto {
    private int id;
    private String metaTitle;
    private String metaKeyword;
    private String metaDescription;
    private String projectName;
    private String state;
    private String city;
    private String projectLocality;
    private String projectConfiguration;
    private String projectBy;
    private String projectPrice;
    private String ivrNo;
    private String reraNo;
    private String reraQr;
    private String reraWebsite;
    private String projectStatus;
    private String propertyType;
    private String country;
    private String slugURL;
    private boolean showFeaturedProperties;
    private String amenityDesc;
    private String floorPlanDesc;
    private String locationDesc;
    private boolean status;
    private String projectThumbnailImage;
    private String projectAddress;
    private String typeName;
    private String projectStatusName;
}
