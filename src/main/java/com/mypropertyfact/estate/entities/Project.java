package com.mypropertyfact.estate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mypropertyfact.estate.enums.ProjectApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@NamedEntityGraph(
        name = "Project.withAllRelations",
        attributeNodes = {
                @NamedAttributeNode("city"),
                @NamedAttributeNode("builder"),
                @NamedAttributeNode("projectTypes"),
                @NamedAttributeNode("projectStatus"),
                @NamedAttributeNode("projectBanners"),
                @NamedAttributeNode("floorPlans"),
                @NamedAttributeNode("amenities"),
                @NamedAttributeNode("projectsAbout"),
                @NamedAttributeNode("projectWalkthrough"),
                @NamedAttributeNode("locationBenefits"),
                @NamedAttributeNode("projectGalleries"),
                @NamedAttributeNode("projectFaqs")
        }
)
@Entity
@Getter
@Setter
@Table(name = "projects")
@ToString(exclude = {
        "city", "builder", "projectTypes", "projectStatus",
        "projectBanners", "floorPlans", "amenities", "projectsAbout",
        "projectWalkthrough", "locationBenefits", "projectGalleries", "projectFaqs"
})
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
    private String projectLocality;
    private String projectConfiguration;
    private String projectPrice;
    private String ivrNo;
    private String locationMap;
    private String reraNo;
    private String reraQr;
    private String reraWebsite;
    private String projectLogo;
    private String projectThumbnail;
    private String slugURL;
    private boolean showFeaturedProperties;
    private String projectThumbnailAltTag;
    private String projectLogoAltTag;
    private String locationMapAltTag;
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_Id")
    @JsonIgnore
    private City city;

    @Lob
    private String amenityDesc;
    @Lob
    private String floorPlanDesc;
    @Lob
    private String locationDesc;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "project_amenities",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @JsonIgnore
    private Set<Amenity> amenities;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ProjectBanner> projectBanners;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<FloorPlan> floorPlans;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private ProjectsAbout projectsAbout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "builder_id")
    @JsonIgnore
    private Builder builder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_type")
    @JsonIgnore
    private ProjectTypes projectTypes;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private ProjectWalkthrough projectWalkthrough;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<LocationBenefit> locationBenefits;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProjectGallery> projectGalleries;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProjectFaqs> projectFaqs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_status_id")
    @JsonIgnore
    private ProjectStatus projectStatus;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProjectMobileBanner> projectMobileBanners;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProjectDesktopBanner> projectDesktopBanners;

    // ========== NEW FIELDS FOR USER PORTAL SUBMISSION ==========
    
    // Additional Property Details
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer balconies;
    private Integer floorNumber;
    private Integer totalFloors;
    private String facing;
    private Integer ageOfConstruction;
    
    // Area Details
    private Double carpetAreaSqft;
    private Double builtUpAreaSqft;
    private Double superBuiltUpAreaSqft;
    private Double plotAreaSqft;
    
    // Pricing Details
    private Double pricePerSqft;
    private Double maintenanceCharges;
    private Double bookingAmount;
    private String furnishedStatus;
    private String parkingDetails;
    private String transactionType; // Sale or Rent
    private String listingType; // Residential or Commercial
    private String propertySubtype; // Apartment, Villa, etc.
    private String possessionStatus;
    private String occupancyStatus;
    private Integer noticePeriod; // In days
    
    // Contact Information
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String preferredTime;
    @Lob
    private String additionalNotes;
    
    // User Submission Tracking Fields
    @Enumerated(EnumType.STRING)
    private ProjectApprovalStatus approvalStatus = ProjectApprovalStatus.APPROVED;
    private Boolean isUserSubmitted = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id")
    @JsonIgnore
    private User submittedBy;
    
    private LocalDateTime submittedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    @JsonIgnore
    private User approvedBy;
    
    private LocalDateTime approvedAt;
    
    @Lob
    private String rejectionReason;

}
