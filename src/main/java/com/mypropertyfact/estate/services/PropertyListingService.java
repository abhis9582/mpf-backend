package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.dtos.PropertyListingDto;
import com.mypropertyfact.estate.dtos.PropertyListingRequestDto;
import com.mypropertyfact.estate.entities.*;
import com.mypropertyfact.estate.enums.ProjectApprovalStatus;
import com.mypropertyfact.estate.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PropertyListingService {
    
    @Autowired
    private PropertyListingRepository propertyListingRepository;
    
    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
    private PropertyListingImageRepository propertyListingImageRepository;
    
    
    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private BuilderRepository builderRepository;
    
    @Autowired
    private LocalityRepository localityRepository;
    
    @Autowired
    private ProjectTypeRepository projectTypeRepository;
    
    @Autowired
    private ProjectStatusRepository projectStatusRepository;
    
    @Autowired
    private AmenityRepository amenityRepository;
    
    @Value("${upload_dir:uploads/}")
    private String uploadDir;
    
    /**
     * Create a new property listing from form data and images
     * Transaction will rollback on any exception
     */
    @Transactional(rollbackFor = Exception.class)
    public PropertyListing createPropertyListing(PropertyListingRequestDto dto, MultipartFile[] images, User user) {
        log.info("Creating property listing for user: {}", user.getEmail());
        
        PropertyListing listing = new PropertyListing();
        
        // ========== BASIC INFORMATION ==========
        listing.setListingType(dto.getListingType());
        listing.setTransaction(dto.getTransaction());
        listing.setSubType(dto.getSubType());
        listing.setTitle(dto.getTitle() != null ? dto.getTitle() : generateTitle(dto));
        listing.setDescription(dto.getDescription());
        listing.setStatus(dto.getStatus());
        listing.setPossession(dto.getPossession());
        listing.setOccupancy(dto.getOccupancy());
        listing.setNoticePeriod(dto.getNoticePeriod());
        
        // ========== LOCATION & AREA ==========
        listing.setProjectName(dto.getProjectName());
        listing.setBuilderName(dto.getBuilderName());
        listing.setAddress(dto.getAddress());
        listing.setLocalityName(dto.getLocality());
        listing.setPincode(dto.getPinCode());
        listing.setLatitude(dto.getLatitude());
        listing.setLongitude(dto.getLongitude());
        
        // Area Details
        listing.setCarpetArea(dto.getCarpetArea());
        listing.setBuiltUpArea(dto.getBuiltUpArea());
        listing.setSuperBuiltUpArea(dto.getSuperBuiltUpArea());
        listing.setPlotArea(dto.getPlotArea());
        
        // ========== PRICING ==========
        listing.setTotalPrice(dto.getTotalPrice());
        listing.setPricePerSqft(dto.getPricePerSqft());
        listing.setMaintenanceCharges(dto.getMaintenanceCam()); // Map maintenanceCam to maintenanceCharges
        listing.setBookingAmount(dto.getBookingAmount());
        
        // ========== PROPERTY DETAILS ==========
        listing.setFloorNumber(dto.getFloorNo());
        listing.setTotalFloors(dto.getTotalFloors());
        listing.setFacing(dto.getFacing());
        listing.setAgeOfConstruction(dto.getAgeOfConstruction());
        
        // ========== CONFIGURATION ==========
        listing.setBedrooms(dto.getBedrooms());
        listing.setBathrooms(dto.getBathrooms());
        listing.setBalconies(dto.getBalconies());
        listing.setParking(dto.getParkingType());
        listing.setFurnished(dto.getFurnishingLevel());
        
        // Features - convert from list to list
        if (dto.getIncludedItems() != null) {
            listing.setFeatures(new ArrayList<>(dto.getIncludedItems()));
        }
        
        // ========== MEDIA & CONTACT ==========
        listing.setVirtualTour(dto.getVideoUrl());
        listing.setOwnershipType(dto.getOwnershipType());
        listing.setReraId(dto.getReraId());
        listing.setReraState(dto.getReraState());
        // Contact information - use contactName/contactPhone/contactEmail, fallback to primaryContact/primaryEmail
        listing.setContactName(dto.getContactName());
        listing.setContactPhone(dto.getContactPhone() != null ? dto.getContactPhone() : 
                                (dto.getPrimaryContact() != null ? dto.getPrimaryContact() : null));
        listing.setContactEmail(dto.getContactEmail() != null ? dto.getContactEmail() : 
                                (dto.getPrimaryEmail() != null ? dto.getPrimaryEmail() : null));
        listing.setContactPreference(dto.getContactPreference() != null ? dto.getContactPreference() : "Phone");
        listing.setPreferredTime(dto.getPreferredTime());
        listing.setAdditionalNotes(dto.getAdditionalNotes());
        listing.setTruthfulDeclaration(dto.getTruthfulDeclaration() != null ? dto.getTruthfulDeclaration() : true);
        listing.setDpdpConsent(dto.getDpdpConsent() != null ? dto.getDpdpConsent() : true);
        
        // ========== RELATIONSHIPS ==========
        listing.setUser(user);
        
        // Set City
        if (dto.getCityId() != null) {
            cityRepository.findById(dto.getCityId()).ifPresent(listing::setCity);
        } else if (dto.getCity() != null) {
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
            listing.setCity(city);
        }
        
        // Set Builder
        if (dto.getBuilderId() != null) {
            builderRepository.findById(dto.getBuilderId()).ifPresent(listing::setBuilder);
        } else if (dto.getBuilderName() != null && !dto.getBuilderName().isEmpty()) {
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
            listing.setBuilder(builder);
        }
        
        // Set Locality
        if (dto.getLocalityId() != null) {
            localityRepository.findById((long) dto.getLocalityId()).ifPresent(listing::setLocality);
        }
        
        // Set Listing Type Entity (ProjectTypes)
        if (dto.getListingType() != null) {
            projectTypeRepository.findByProjectTypeNameIgnoreCase(dto.getListingType())
                .ifPresent(listing::setListingTypeEntity);
        }
        
        // Set Status Entity (ProjectStatus) - try to find by status name
        if (dto.getStatus() != null) {
            projectStatusRepository.findByStatusNameIgnoreCase(dto.getStatus())
                .ifPresent(listing::setStatusEntity);
            // If not found by name, status will remain as string in listing.status field
        }
        
        // Set Amenities
        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            Set<Amenity> amenitySet = dto.getAmenityIds().stream()
                .map(amenityId -> amenityRepository.findById(amenityId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            listing.setAmenities(amenitySet);
        } else if (dto.getSocietyFeatures() != null && !dto.getSocietyFeatures().isEmpty()) {
            // Handle amenity names if IDs not provided
            Set<Amenity> amenitySet = new HashSet<>();
            for (String amenityName : dto.getSocietyFeatures()) {
                    Amenity amenity = amenityRepository.findByTitleIgnoreCase(amenityName)
                    .orElseGet(() -> {
                        log.info("Creating new amenity: {}", amenityName);
                        Amenity newAmenity = new Amenity();
                        newAmenity.setTitle(amenityName);
                        newAmenity.setAltTag(amenityName.toLowerCase().replace(" ", "-"));
                        newAmenity.setStatus(true);
                        return amenityRepository.save(newAmenity);
                    });
                amenitySet.add(amenity);
            }
            listing.setAmenities(amenitySet);
        }
        
        // Set approval status to PENDING
        listing.setApprovalStatus(ProjectApprovalStatus.PENDING);
        
        try {
            // Save the listing first to get the ID
            PropertyListing savedListing = propertyListingRepository.save(listing);
            
            // ========== HANDLE IMAGES ==========
            if (images != null && images.length > 0) {
                savePropertyImages(savedListing, images);
            }
            
            log.info("Property listing created successfully with ID: {}", savedListing.getId());
            return savedListing;
        } catch (Exception e) {
            log.error("Error creating property listing: {}", e.getMessage(), e);
            // Exception will trigger transaction rollback due to @Transactional(rollbackFor = Exception.class)
            throw new RuntimeException("Failed to create property listing: " + e.getMessage(), e);
        }
    }
    
    /**
     * Save images for a property listing
     * Note: This method should be called within an existing transaction
     * to ensure rollback if image saving fails
     */
    public void savePropertyImages(PropertyListing listing, MultipartFile[] images) {
        String listingDir = null;
        Path listingPath = null;
        List<Path> savedFiles = new ArrayList<>();
        
        try {
            // Create directory for property listing images
            listingDir = uploadDir + "property-listings" + File.separator + listing.getId() + File.separator;
            listingPath = Paths.get(listingDir);
            Files.createDirectories(listingPath);
            
            for (int i = 0; i < images.length; i++) {
                MultipartFile imageFile = images[i];
                if (imageFile == null || imageFile.isEmpty()) {
                    continue;
                }
                
                // Generate unique filename
                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String fileName = System.currentTimeMillis() + "_" + i + fileExtension;
                
                // Save file
                Path filePath = listingPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath);
                savedFiles.add(filePath);
                
                // Create image entity
                PropertyListingImage image = new PropertyListingImage();
                // Store relative path for easier URL construction
                // Format: property-listings/{id}/{filename}
                String relativePath = "property-listings" + File.separator + listing.getId() + File.separator + fileName;
                image.setImageUrl(relativePath);
                image.setImageName(originalFilename);
                image.setImageSize(imageFile.getSize());
                image.setMimeType(imageFile.getContentType());
                image.setDisplayOrder(i);
                image.setIsPrimary(i == 0); // First image is primary
                image.setPropertyListing(listing);
                
                propertyListingImageRepository.save(image);
                
                log.info("Saved image {} for listing {}", fileName, listing.getId());
            }
        } catch (IOException e) {
            log.error("Error saving images for listing {}: {}", listing.getId(), e.getMessage(), e);
            // Cleanup: Delete any files that were saved before the error
            cleanupSavedFiles(savedFiles);
            throw new RuntimeException("Failed to save images: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error saving images for listing {}: {}", listing.getId(), e.getMessage(), e);
            // Cleanup: Delete any files that were saved before the error
            cleanupSavedFiles(savedFiles);
            throw new RuntimeException("Failed to save images: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cleanup saved files if transaction fails
     */
    private void cleanupSavedFiles(List<Path> savedFiles) {
        for (Path filePath : savedFiles) {
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("Cleaned up file: {}", filePath);
                }
            } catch (IOException e) {
                log.warn("Failed to cleanup file {}: {}", filePath, e.getMessage());
            }
        }
    }
    
    /**
     * Get property listing by ID
     */
    @Transactional(readOnly = true)
    public Optional<PropertyListingDto> getPropertyListingById(Long id) {
        return propertyListingRepository.findById(id)
            .map(this::convertToDto);
    }
    
    /**
     * Get all property listings for a user
     */
    @Transactional(readOnly = true)
    public List<PropertyListingDto> getUserPropertyListings(Integer userId) {
        List<PropertyListing> listings = propertyListingRepository.findByUserId(userId);
        return listings.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Get property listings by approval status
     */
    @Transactional(readOnly = true)
    public List<PropertyListingDto> getPropertyListingsByStatus(ProjectApprovalStatus status) {
        List<PropertyListing> listings = propertyListingRepository.findByApprovalStatus(status);
        return listings.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all approved property listings with optional filters (public access)
     */
    @Transactional(readOnly = true)
    public List<PropertyListingDto> getApprovedPropertyListings(
            String city, String listingType, String transaction, Integer bedrooms,
            String status, Double minPrice, Double maxPrice, String subType) {
        
        List<PropertyListing> listings = propertyListingRepository.findByApprovalStatus(ProjectApprovalStatus.APPROVED);
        
        // Apply filters
        return listings.stream()
            .filter(listing -> {
                if (city != null && !city.isEmpty()) {
                    if (listing.getCity() == null || !listing.getCity().getName().equalsIgnoreCase(city)) {
                        return false;
                    }
                }
                if (listingType != null && !listingType.isEmpty()) {
                    if (!listingType.equalsIgnoreCase(listing.getListingType())) {
                        return false;
                    }
                }
                if (transaction != null && !transaction.isEmpty()) {
                    if (!transaction.equalsIgnoreCase(listing.getTransaction())) {
                        return false;
                    }
                }
                if (bedrooms != null) {
                    if (listing.getBedrooms() == null || !listing.getBedrooms().equals(bedrooms)) {
                        return false;
                    }
                }
                if (status != null && !status.isEmpty()) {
                    if (!status.equalsIgnoreCase(listing.getStatus())) {
                        return false;
                    }
                }
                if (minPrice != null && listing.getTotalPrice() != null) {
                    if (listing.getTotalPrice() < minPrice) {
                        return false;
                    }
                }
                if (maxPrice != null && listing.getTotalPrice() != null) {
                    if (listing.getTotalPrice() > maxPrice) {
                        return false;
                    }
                }
                if (subType != null && !subType.isEmpty()) {
                    if (!subType.equalsIgnoreCase(listing.getSubType())) {
                        return false;
                    }
                }
                return true;
            })
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Update property listing
     */
    @Transactional
    public PropertyListing updatePropertyListing(Long id, PropertyListingRequestDto dto, MultipartFile[] images, User user) {
        // Check if user is admin - admins can update any listing
        boolean isAdmin = userRoleService.userHasRole(user.getId(), "SUPERADMIN");
        
        PropertyListing listing;
        if (isAdmin) {
            listing = propertyListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property listing not found"));
        } else {
            listing = propertyListingRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Property listing not found or unauthorized"));
        }
        
        // Update fields (similar to create, but only update provided fields)
        if (dto.getTitle() != null) listing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) listing.setDescription(dto.getDescription());
        if (dto.getTotalPrice() != null) listing.setTotalPrice(dto.getTotalPrice());
        // ... update other fields as needed
        
        // Update images if provided
        if (images != null && images.length > 0) {
            // Delete existing images
            propertyListingImageRepository.deleteByPropertyListingId(id);
            // Save new images
            savePropertyImages(listing, images);
        }
        
        return propertyListingRepository.save(listing);
    }
    
    /**
     * Delete property listing
     */
    @Transactional
    public void deletePropertyListing(Long id, User user) {
        // Check if user is admin - admins can delete any listing
        boolean isAdmin = userRoleService.userHasRole(user.getId(), "SUPERADMIN");
        
        PropertyListing listing;
        if (isAdmin) {
            listing = propertyListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property listing not found"));
        } else {
            listing = propertyListingRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Property listing not found or unauthorized"));
        }
        
        // Delete images
        propertyListingImageRepository.deleteByPropertyListingId(id);
        
        // Delete listing
        propertyListingRepository.delete(listing);
    }
    
    /**
     * Approve property listing (admin only)
     */
    @Transactional
    public PropertyListing approvePropertyListing(Long id, User admin) {
        PropertyListing listing = propertyListingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property listing not found"));
        
        listing.setApprovalStatus(ProjectApprovalStatus.APPROVED);
        listing.setApprovedBy(admin);
        listing.setApprovedAt(LocalDateTime.now());
        
        return propertyListingRepository.save(listing);
    }
    
    /**
     * Reject property listing (admin only)
     */
    @Transactional
    public PropertyListing rejectPropertyListing(Long id, String reason, User admin) {
        PropertyListing listing = propertyListingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property listing not found"));
        
        listing.setApprovalStatus(ProjectApprovalStatus.REJECTED);
        listing.setApprovedBy(admin);
        listing.setApprovedAt(LocalDateTime.now());
        listing.setRejectionReason(reason);
        
        return propertyListingRepository.save(listing);
    }
    
    /**
     * Convert PropertyListing entity to DTO
     */
    private PropertyListingDto convertToDto(PropertyListing listing) {
        PropertyListingDto dto = new PropertyListingDto();
        
        dto.setId(listing.getId());
        dto.setListingType(listing.getListingType());
        dto.setTransaction(listing.getTransaction());
        dto.setSubType(listing.getSubType());
        dto.setTitle(listing.getTitle());
        dto.setDescription(listing.getDescription());
        dto.setStatus(listing.getStatus());
        dto.setPossession(listing.getPossession());
        dto.setOccupancy(listing.getOccupancy());
        dto.setNoticePeriod(listing.getNoticePeriod());
        
        dto.setProjectName(listing.getProjectName());
        dto.setBuilderName(listing.getBuilderName());
        dto.setBuilderId(listing.getBuilder() != null ? listing.getBuilder().getId() : null);
        dto.setAddress(listing.getAddress());
        dto.setLocality(listing.getLocalityName());
        dto.setLocalityId(listing.getLocality() != null ? (int) listing.getLocality().getId() : null);
        dto.setCity(listing.getCity() != null ? listing.getCity().getName() : null);
        dto.setCityId(listing.getCity() != null ? listing.getCity().getId() : null);
        dto.setPincode(listing.getPincode());
        dto.setLatitude(listing.getLatitude());
        dto.setLongitude(listing.getLongitude());
        
        dto.setCarpetArea(listing.getCarpetArea());
        dto.setBuiltUpArea(listing.getBuiltUpArea());
        dto.setSuperBuiltUpArea(listing.getSuperBuiltUpArea());
        dto.setPlotArea(listing.getPlotArea());
        
        dto.setTotalPrice(listing.getTotalPrice());
        dto.setPricePerSqft(listing.getPricePerSqft());
        dto.setMaintenanceCharges(listing.getMaintenanceCharges());
        dto.setBookingAmount(listing.getBookingAmount());
        
        dto.setFloorNumber(listing.getFloorNumber());
        dto.setTotalFloors(listing.getTotalFloors());
        dto.setFacing(listing.getFacing());
        dto.setAgeOfConstruction(listing.getAgeOfConstruction());
        
        dto.setBedrooms(listing.getBedrooms());
        dto.setBathrooms(listing.getBathrooms());
        dto.setBalconies(listing.getBalconies());
        dto.setParking(listing.getParking());
        dto.setFurnished(listing.getFurnished());
        // Initialize lazy-loaded features collection before accessing
        if (listing.getFeatures() != null) {
            Hibernate.initialize(listing.getFeatures());
            dto.setFeatures(new ArrayList<>(listing.getFeatures()));
        } else {
            dto.setFeatures(new ArrayList<>());
        }
        
        // Initialize lazy-loaded amenities collection before accessing
        if (listing.getAmenities() != null) {
            Hibernate.initialize(listing.getAmenities());
            dto.setAmenityIds(listing.getAmenities().stream()
                .map(Amenity::getId)
                .collect(Collectors.toList()));
            dto.setAmenityNames(listing.getAmenities().stream()
                .map(Amenity::getTitle)
                .collect(Collectors.toList()));
        }
        
        // Get image URLs
        List<PropertyListingImage> images = propertyListingImageRepository
            .findByPropertyListingIdOrderByDisplayOrderAsc(listing.getId());
        dto.setImageUrls(images.stream()
            .map(PropertyListingImage::getImageUrl)
            .collect(Collectors.toList()));
        
        dto.setVirtualTour(listing.getVirtualTour());
        dto.setOwnershipType(listing.getOwnershipType());
        dto.setReraId(listing.getReraId());
        dto.setReraState(listing.getReraState());
        dto.setContactName(listing.getContactName());
        dto.setContactPhone(listing.getContactPhone());
        dto.setContactEmail(listing.getContactEmail());
        dto.setContactPreference(listing.getContactPreference());
        dto.setPreferredTime(listing.getPreferredTime());
        dto.setAdditionalNotes(listing.getAdditionalNotes());
        dto.setTruthfulDeclaration(listing.getTruthfulDeclaration());
        dto.setDpdpConsent(listing.getDpdpConsent());
        
        dto.setApprovalStatus(listing.getApprovalStatus());
        dto.setRejectionReason(listing.getRejectionReason());
        dto.setCreatedAt(listing.getCreatedAt());
        dto.setUpdatedAt(listing.getUpdatedAt());
        dto.setApprovedAt(listing.getApprovedAt());
        
        dto.setUserId(listing.getUser().getId());
        dto.setUserEmail(listing.getUser().getEmail());
        
        return dto;
    }
    
    /**
     * Generate title from property details
     */
    private String generateTitle(PropertyListingRequestDto dto) {
        List<String> parts = new ArrayList<>();
        if (dto.getBedrooms() != null) parts.add(dto.getBedrooms() + " BHK");
        if (dto.getSubType() != null) parts.add(dto.getSubType());
        if (dto.getLocality() != null) parts.add("in " + dto.getLocality());
        if (dto.getCity() != null) parts.add(dto.getCity());
        return parts.isEmpty() ? "Property Listing" : String.join(" ", parts);
    }
    
    /**
     * Generate slug from text
     */
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

