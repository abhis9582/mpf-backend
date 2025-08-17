package com.mypropertyfact.estate.dtos;

import com.mypropertyfact.estate.configs.dtos.*;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDetailDto {
    private int id;
    private int builderId;
    private int cityId;
    private int stateId;
    private int countryId;
    private int propertyTypeId;
    private int projectStatusId;
    private String projectPrice;
    private String metaTitle;
    private String metaKeyword;
    private String metaDescription;
    private String projectName;
    private String slugURL;
    private String projectLocality;
    private String projectConfiguration;
    private String ivrNo;
    private String reraNo;
    private String reraQr;
    private String reraWebsite;
    private String floorPlanDescription;
    private String locationDescription;
    private String amenityDescription;
    private boolean showFeaturedProperties;
    private boolean status;
    private String builderName;
    private String projectStatusName;
    private String propertyTypeName;
    private String cityName;
    private String stateName;
    private String countryName;
    private String locationMapImage;
    private String projectLogoImage;
    private String projectThumbnailImage;
    private String projectAddress;
    private String projectAboutShortDescription;
    private String projectAboutLongDescription;
    private String projectWalkthroughDescription;
    private List<ProjectBannerDto> projectBannerList;
    private List<FloorPlanDto> projectFloorPlanList;
    private List<AmenityDto> projectAmenityList;
    private List<ProjectGalleryDto> projectGalleryImageList;
    private List<LocationBenefitDto> projectLocationBenefitList;
    private List<ProjectFaqDto> projectFaqList;
    private List<ProjectMobileBannerDto> projectMobileBannerDtoList;
    private List<ProjectDesktopBannerDto> projectDesktopBannerDtoList;
}
