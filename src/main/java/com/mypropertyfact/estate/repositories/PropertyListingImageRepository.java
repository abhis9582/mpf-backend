package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.PropertyListing;
import com.mypropertyfact.estate.entities.PropertyListingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyListingImageRepository extends JpaRepository<PropertyListingImage, Long> {
    
    // Find all images for a property listing
    List<PropertyListingImage> findByPropertyListing(PropertyListing propertyListing);
    
    // Find all images for a property listing ID
    List<PropertyListingImage> findByPropertyListingId(Long propertyListingId);
    
    // Find primary image for a property listing
    Optional<PropertyListingImage> findByPropertyListingAndIsPrimaryTrue(PropertyListing propertyListing);
    
    // Find images ordered by display order
    List<PropertyListingImage> findByPropertyListingIdOrderByDisplayOrderAsc(Long propertyListingId);
    
    // Delete all images for a property listing
    void deleteByPropertyListingId(Long propertyListingId);
}


