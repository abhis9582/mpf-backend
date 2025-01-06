package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String metaTitle;
    @Lob
    private String metaKeyword;
    @Lob
    private String metaDescription;
    private String projectName;
    private String projectAddress;
    private String state;
    private String cityLocation;
    private String projectLocality;
    private String projectConfiguration;
    private String projectBy;
    private String projectPrice;
    private String ivrNo;
    private String locationMap;
    private String reraNo;
    private String reraQr;
    private String reraWebsite;
    private String projectStatus;
    private String projectLogo;
    private String projectThumbnail;
    private String propertyType;
    private String slugURL;
    private boolean showFeaturedProperties;
    private boolean status;
    private String country;
    @Lob
    private String amenityDesc;
    @Lob
    private String floorPlanDesc;
    @Lob
    private String locationDesc;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

}
