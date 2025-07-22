package com.mypropertyfact.estate.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProjectDto {
    private int id;
    private String metaTitle;
    private String metaKeyword;
    private String metaDescription;
    private String projectName;
//    private String projectAddress;
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
    private List<AmenityDto> amenities;
    private String projectAddress;
    private String typeName;
    private String projectStatusName;
}
