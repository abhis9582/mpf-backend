package com.mypropertyfact.estate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypropertyfact.estate.dtos.UserPropertySubmissionDto;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.User;
import com.mypropertyfact.estate.enums.ProjectApprovalStatus;
import com.mypropertyfact.estate.services.UserPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/properties")
@Slf4j
public class UserPropertyController {
    
    private final UserPropertyService userPropertyService;
    private final ObjectMapper objectMapper;
    
    public UserPropertyController(UserPropertyService userPropertyService, ObjectMapper objectMapper) {
        this.userPropertyService = userPropertyService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Submit a new property (saves as DRAFT)
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> submitProperty(
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @RequestPart("property") String propertyJson) {
        
        try {
            log.info("Received property submission request");
            
            // Parse JSON string to DTO
            UserPropertySubmissionDto propertyDto = objectMapper.readValue(propertyJson, UserPropertySubmissionDto.class);
            
            // Get current authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            
            // Submit property (saves as DRAFT)
            Project savedProject = userPropertyService.submitProperty(propertyDto, images, currentUser);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Property saved successfully");
            response.put("propertyId", savedProject.getId());
            response.put("data", savedProject);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error submitting property: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to submit property: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Submit property for admin approval (changes status from DRAFT to PENDING)
     */
    @PutMapping("/{id}/submit")
    public ResponseEntity<?> submitForApproval(@PathVariable int id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            
            Project project = userPropertyService.submitForApproval(id, currentUser.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Property submitted for approval");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error submitting for approval: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to submit for approval: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get all properties submitted by current user
     */
    @GetMapping
    public ResponseEntity<?> getUserProperties() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching user properties: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch properties: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get properties by approval status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPropertiesByStatus(@PathVariable String status) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            
            ProjectApprovalStatus approvalStatus = ProjectApprovalStatus.valueOf(status.toUpperCase());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("status", status);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching properties by status: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch properties: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get single property details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPropertyDetails(@PathVariable int id) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching property details: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch property: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

