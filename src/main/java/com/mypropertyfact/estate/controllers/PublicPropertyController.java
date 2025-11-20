package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.dtos.PropertyListingDto;
import com.mypropertyfact.estate.services.PropertyListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Public controller for property listings
 * No authentication required - for public property browsing
 */
@RestController
@RequestMapping("/api/public/properties")
@Slf4j
public class PublicPropertyController {
    
    private final PropertyListingService propertyListingService;
    
    public PublicPropertyController(PropertyListingService propertyListingService) {
        this.propertyListingService = propertyListingService;
    }
    
    /**
     * Get all approved property listings (public access)
     * GET /api/public/properties
     */
    @GetMapping
    public ResponseEntity<?> getAllApprovedProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String listingType,
            @RequestParam(required = false) String transaction,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String subType) {
        
        try {
            List<PropertyListingDto> listings = propertyListingService.getApprovedPropertyListings(
                city, listingType, transaction, bedrooms, status, minPrice, maxPrice, subType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", listings.size());
            response.put("properties", listings);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching approved properties: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch properties: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get property listing by ID (public access)
     * GET /api/public/properties/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPropertyById(@PathVariable Long id) {
        try {
            java.util.Optional<PropertyListingDto> listingDto = propertyListingService.getPropertyListingById(id);
            
            if (listingDto.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Property listing not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            PropertyListingDto property = listingDto.get();
            
            // Only return approved properties
            if (property.getApprovalStatus() == null || 
                !property.getApprovalStatus().toString().equals("APPROVED")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Property listing not available");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("property", property);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching property by ID: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch property: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

