package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.dtos.UserPropertySubmissionDto;
import com.mypropertyfact.estate.entities.*;
import com.mypropertyfact.estate.enums.ProjectApprovalStatus;
import com.mypropertyfact.estate.repositories.*;
import com.mypropertyfact.estate.common.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class UserPropertyService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private BuilderRepository builderRepository;
    
    @Autowired
    private ProjectTypeRepository projectTypeRepository;
    
    @Autowired
    private AmenityRepository amenityRepository;
    
    @Autowired
    private FileUtils fileUtils;
    
    @Value("${upload_dir}")
    private String uploadDir;
    
    @Transactional
    public Project submitProperty(UserPropertySubmissionDto dto, MultipartFile[] images, User user) {
        log.info("Submitting property for user: {}", user.getEmail());
        
        // Create new Project
        Project project = new Project();
        
        // Map basic information
        project.setProjectName(dto.getTitle() != null ? dto.getTitle() : 
            generateTitle(dto.getBedrooms(), dto.getSubType(), dto.getLocality()));
        project.setProjectLocality(dto.getLocality());
        
        // Set configuration (e.g., "3 BHK")
        if (dto.getBedrooms() != null) {
            project.setProjectConfiguration(dto.getBedrooms() + " BHK");
        }
        
        // Set price
        if (dto.getTotalPrice() != null) {
            project.setProjectPrice(dto.getTotalPrice().toString());
        }
        
        // Generate slug URL
        project.setSlugURL(generateSlug(dto.getTitle() != null ? dto.getTitle() : 
            generateTitle(dto.getBedrooms(), dto.getSubType(), dto.getLocality())));
        
        // Initially inactive until approved
        project.setStatus(false);
        
        // Set approval status to DRAFT (user can submit later)
        project.setApprovalStatus(ProjectApprovalStatus.DRAFT);
        project.setIsUserSubmitted(true);
        project.setSubmittedBy(user);
        project.setSubmittedAt(LocalDateTime.now());
        
        // Map description
        project.setLocationDesc(dto.getDescription());
        
        // Map location/builder - Find or create City
        if (dto.getCity() != null) {
            City city = cityRepository.findByNameIgnoreCase(dto.getCity())
                .orElseGet(() -> {
                    log.info("Creating new city: {}", dto.getCity());
                    City newCity = new City();
                    newCity.setName(dto.getCity());
                    newCity.setSlugUrl(generateSlug(dto.getCity()));
                    newCity.setCreatedAt(LocalDateTime.now());
                    newCity.setUpdatedAt(LocalDateTime.now());
                    return cityRepository.save(newCity);
                });
            project.setCity(city);
        }
        
        // Find or create Builder
        if (dto.getBuilderName() != null && !dto.getBuilderName().isEmpty()) {
            Builder builder = builderRepository.findByBuilderNameIgnoreCase(dto.getBuilderName())
                .orElseGet(() -> {
                    log.info("Creating new builder: {}", dto.getBuilderName());
                    Builder newBuilder = new Builder();
                    newBuilder.setBuilderName(dto.getBuilderName());
                    newBuilder.setSlugUrl(generateSlug(dto.getBuilderName()));
                    newBuilder.setCreatedAt(LocalDateTime.now());
                    newBuilder.setUpdatedAt(LocalDateTime.now());
                    return builderRepository.save(newBuilder);
                });
            project.setBuilder(builder);
        }
        
        // Find Project Type (property subtype)
        if (dto.getSubType() != null) {
            ProjectTypes projectType = projectTypeRepository.findByProjectTypeNameIgnoreCase(dto.getSubType())
                .orElse(null);
            project.setProjectTypes(projectType);
        }
        
        // Save uploaded images
        Set<ProjectGallery> galleries = new HashSet<>();
        if (images != null && images.length > 0) {
            log.info("Processing {} images", images.length);
            
            String galleryDir = uploadDir + "project-gallery" + File.separator;
            
            for (int i = 0; i < images.length; i++) {
                try {
                    MultipartFile image = images[i];
                    
                    // Save image
                    String imagePath = fileUtils.saveOriginalImage(image, galleryDir);
                    
                    ProjectGallery gallery = new ProjectGallery();
                    gallery.setImage(imagePath);
                    gallery.setSlugUrl(imagePath);
                    gallery.setProject(project);
                    
                    // First image is thumbnail
                    if (i == 0) {
                        project.setProjectThumbnail(imagePath);
                    }
                    
                    galleries.add(gallery);
                } catch (Exception e) {
                    log.error("Error uploading image: {}", e.getMessage());
                }
            }
            project.setProjectGalleries(galleries);
        }
        
        // Set amenities if provided
        if (dto.getAmenities() != null && !dto.getAmenities().isEmpty()) {
            Set<Amenity> amenitySet = new HashSet<>();
            for (Integer amenityId : dto.getAmenities()) {
                amenityRepository.findById(amenityId).ifPresent(amenitySet::add);
            }
            project.setAmenities(amenitySet);
        }
        
        // Map all new detailed fields
        project.setBedrooms(dto.getBedrooms());
        project.setBathrooms(dto.getBathrooms());
        project.setBalconies(dto.getBalconies());
        project.setFloorNumber(dto.getFloor());
        project.setTotalFloors(dto.getTotalFloors());
        project.setFacing(dto.getFacing());
        project.setAgeOfConstruction(dto.getAgeOfConstruction());
        
        // Area details
        project.setCarpetAreaSqft(dto.getCarpetArea());
        project.setBuiltUpAreaSqft(dto.getBuiltUpArea());
        project.setSuperBuiltUpAreaSqft(dto.getSuperBuiltUpArea());
        project.setPlotAreaSqft(dto.getPlotArea());
        
        // Pricing details
        project.setPricePerSqft(dto.getPricePerSqFt());
        project.setMaintenanceCharges(dto.getMaintenanceCharges());
        project.setBookingAmount(dto.getBookingAmount());
        project.setFurnishedStatus(dto.getFurnished());
        project.setParkingDetails(dto.getParking());
        
        // Property type and transaction
        project.setTransactionType(dto.getTransaction());
        project.setListingType(dto.getListingType());
        project.setPropertySubtype(dto.getSubType());
        project.setPossessionStatus(dto.getPossession());
        project.setOccupancyStatus(dto.getOccupancy());
        project.setNoticePeriod(dto.getNoticePeriod());
        
        // Contact information
        project.setContactName(dto.getContactName());
        project.setContactPhone(dto.getContactPhone());
        project.setContactEmail(dto.getContactEmail());
        project.setPreferredTime(dto.getPreferredTime());
        project.setAdditionalNotes(dto.getAdditionalNotes());
        
        // Set timestamps
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        
        // Save project
        Project savedProject = projectRepository.save(project);
        log.info("Property saved successfully with ID: {}", savedProject.getId());
        
        return savedProject;
    }
    
    @Transactional
    public Project submitForApproval(int projectId, int userId) {
        log.info("Submitting property {} for approval by user {}", projectId, userId);
        
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Verify ownership
        if (!project.getSubmittedBy().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You don't own this property");
        }
        
        // Change status to PENDING
        project.setApprovalStatus(ProjectApprovalStatus.PENDING);
        project.setSubmittedAt(LocalDateTime.now());
        
        return projectRepository.save(project);
    }
    
    @Transactional
    public List<Project> getUserProperties(int userId) {
        log.info("Fetching properties for user: {}", userId);
        return projectRepository.findBySubmittedById(userId);
    }
    
    @Transactional
    public List<Project> getUserPropertiesByStatus(int userId, ProjectApprovalStatus status) {
        log.info("Fetching properties with status {} for user: {}", status, userId);
        return projectRepository.findBySubmittedByIdAndApprovalStatus(userId, status);
    }
    
    private String generateTitle(Integer bedrooms, String subType, String locality) {
        if (bedrooms != null && subType != null && locality != null) {
            return bedrooms + " BHK " + subType + " in " + locality;
        }
        return "Property";
    }
    
    private String generateSlug(String text) {
        if (text == null) {
            return UUID.randomUUID().toString();
        }
        return text.toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "")
            + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}

