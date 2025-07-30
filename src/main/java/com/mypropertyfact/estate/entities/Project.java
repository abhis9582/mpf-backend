package com.mypropertyfact.estate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

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
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "city_Id")
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
    private List<Amenity> amenities;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProjectBanner> projectBanners;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FloorPlan> floorPlans;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private ProjectsAbout projectsAbout;

    @ManyToOne
    @JoinColumn(name = "builder_id")
    private Builder builder;

    @ManyToOne
    @JoinColumn(name = "property_type")
    private ProjectTypes projectTypes;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private ProjectWalkthrough projectWalkthrough;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<LocationBenefit> locationBenefits;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectGallery> projectGalleries;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectFaqs> projectFaqs;

    @ManyToOne
    @JoinColumn(name = "project_status_id")
    private ProjectStatus projectStatus;

}
