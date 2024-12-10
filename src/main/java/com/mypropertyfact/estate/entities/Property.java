package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 1000)
    private String metaTitle;
    @Column(length = 2000)
    private String metaKeyword;
    @Column(length = 3000)
    private String metaDescription;
    private String projectName;
    private String projectAddress;
    private String state;
    private String cityLocation;
    private String projectLocality;
    private String projectConfiguration;
    private String projectBy;
    private String projectPrice;
    private long ivrNo;
    private String locationMap;
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
    private String projectLogo;
    private String projectThumbnail;
    private String propertyType;
    private String slugURL;
    private String showSimilarProperties;
    private boolean status;
    private String country;
    private String showFeaturedProperties;
    @Column(columnDefinition = "TEXT")
    private String aboutDesc;
    @Column(columnDefinition = "TEXT")
    private String walkthroughDesc;
    @Column(columnDefinition = "TEXT")
    private String floorPlanDesc;
    @Column(columnDefinition = "TEXT")
    private String amenityDesc;
    @Column(columnDefinition = "TEXT")
    private String locationDesc;
//    private String status;

}
