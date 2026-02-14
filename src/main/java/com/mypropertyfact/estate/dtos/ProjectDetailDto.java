package com.mypropertyfact.estate.dtos;

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
    private String builderSlugURL;
    private String builderDescription;
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
    
    // User-submitted property fields
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer balconies;
    private Integer floorNumber;
    private Integer totalFloors;
    private String facing;
    private Integer ageOfConstruction;
    private Double carpetAreaSqft;
    private Double builtUpAreaSqft;
    private Double superBuiltUpAreaSqft;
    private Double plotAreaSqft;
    private Double pricePerSqft;
    private Double maintenanceCharges;
    private Double bookingAmount;
    private String furnishedStatus;
    private String parkingDetails;
    private String transactionType;
    private String listingType;
    private String propertySubtype;
    private String possessionStatus;
    private String occupancyStatus;
    private Integer noticePeriod;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String preferredTime;
    private String additionalNotes;
    private Double totalPrice; // Total price as number (in addition to projectPrice string)
}
