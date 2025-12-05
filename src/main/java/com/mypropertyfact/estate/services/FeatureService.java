package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.FeatureDto;
import com.mypropertyfact.estate.dtos.FeatureDetailedDto;
import com.mypropertyfact.estate.entities.Feature;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeatureService {
    
    @Autowired
    private FeatureRepository featureRepository;
    
    @Autowired
    private FileUtils fileUtils;
    
    @Value("${upload_dir}")
    private String uploadDir;
    
    @Transactional
    public List<Feature> getAllFeatures() {
        return featureRepository.findAllOrderByTitleAsc();
    }
    
    @Transactional
    public Response postFeature(FeatureDto featureDto) {
        Response response = new Response();
        
        // Validate feature data
        if (featureDto.getTitle() == null || featureDto.getTitle().trim().isEmpty()) {
            response.setMessage("Feature title is required!");
            return response;
        }
        
        String title = featureDto.getTitle().trim();
        
        // Check for duplicate title (case-insensitive)
        Optional<Feature> existingFeature = featureRepository.findByTitleIgnoreCase(title);
        
        if (featureDto.getId() != null && featureDto.getId() > 0) {
            // Update existing feature
            Optional<Feature> dbFeature = featureRepository.findById(featureDto.getId());
            if (dbFeature.isEmpty()) {
                response.setMessage("Feature not found!");
                return response;
            }
            
            Feature feature = dbFeature.get();
            
            // Check if title is being changed and if new title already exists
            if (!feature.getTitle().equalsIgnoreCase(title) && existingFeature.isPresent()) {
                response.setMessage("Feature with this title already exists!");
                return response;
            }
            
            feature.setTitle(title);
            feature.setDescription(featureDto.getDescription() != null ? featureDto.getDescription().trim() : null);
            feature.setStatus(featureDto.getStatus() != null ? featureDto.getStatus() : true);
            feature.setUpdatedAt(LocalDateTime.now());
            
            featureRepository.save(feature);
            response.setMessage("Feature updated successfully!");
            response.setIsSuccess(1);
        } else {
            // Create new feature
            if (existingFeature.isPresent()) {
                response.setMessage("Feature with this title already exists!");
                return response;
            }
            
            Feature feature = new Feature();
            feature.setTitle(title);
            feature.setDescription(featureDto.getDescription() != null ? featureDto.getDescription().trim() : null);
            feature.setStatus(featureDto.getStatus() != null ? featureDto.getStatus() : true);
            feature.setCreatedAt(LocalDateTime.now());
            feature.setUpdatedAt(LocalDateTime.now());
            
            featureRepository.save(feature);
            response.setMessage("Feature saved successfully!");
            response.setIsSuccess(1);
        }
        
        return response;
    }
    
    @Transactional
    public Response deleteFeature(Long id) {
        Optional<Feature> dbFeature = featureRepository.findById(id);
        
        if (dbFeature.isEmpty()) {
            return new Response(0, "Feature not found.", 0);
        }
        
        Feature feature = dbFeature.get();
        
        // Delete image if present
        if (feature.getIconImageUrl() != null && !feature.getIconImageUrl().isEmpty()) {
            String featureUploadDir = uploadDir.concat("feature/");
            fileUtils.deleteFileFromDestination(feature.getIconImageUrl(), featureUploadDir);
        }
        
        featureRepository.delete(feature);
        return new Response(1, "Feature deleted successfully!", 0);
    }
    
    @Transactional
    public Response postMultipleFeatures(FeatureDetailedDto dto) {
        Response response = new Response();
        List<String> savedImages = new ArrayList<>();
        String featureImage = "";
        String featureUploadDir = uploadDir.concat("feature/");
        
        try {
            if (dto.getFeaturesFiles() == null || dto.getFeaturesFiles().isEmpty()) {
                response.setMessage("Features are required !");
                response.setIsSuccess(0);
                return response;
            }
            
            // Validate all files are images
            dto.getFeaturesFiles().forEach(file -> {
                if (!fileUtils.isTypeImage(file)) {
                    throw new IllegalArgumentException("Feature file should be image only.");
                }
            });

            for (MultipartFile featureFile : dto.getFeaturesFiles()) {
                // Validate aspect ratio (1:1 ratio for icons, similar to amenities)
                if (!fileUtils.isValidAspectRatio(featureFile.getInputStream(), 100, 100)) {
                    throw new IllegalArgumentException("Feature image should be in 1:1 ratio or 100x100 only !");
                }
                
                // Save and resize the image
                String savedFeatureImage = fileUtils.saveDesktopImageWithResize(featureFile, featureUploadDir, 100, 100, 1.0f);
                savedImages.add(savedFeatureImage);
                featureImage = savedFeatureImage;
                
                // Generate title from filename
                String originalFilename = featureFile.getOriginalFilename();
                String featureTitle;
                if (originalFilename != null && !originalFilename.isEmpty()) {
                    String fileNameWithoutExt = originalFilename.replaceFirst("[.][^.]+$", "");
                    String cleaned = fileNameWithoutExt.replace("_", " ").replaceAll("\\s+", " ").trim();
                    featureTitle = Arrays.stream(cleaned.split(" "))
                            .filter(word -> !word.isBlank())
                            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                            .collect(Collectors.joining(" "));
                    dto.setFeatureName(featureTitle);
                } else {
                    dto.setFeatureName("Feature Image");
                }
                
                dto.setFeatureImage(featureImage);
                
                // Generate alt tag from filename
                String featureAltTag = fileUtils.generateImageAltTag(featureFile);
                dto.setFeatureAltTag(featureAltTag);

                // Check if feature with this title already exists
                Optional<Feature> existingFeature = featureRepository.findByTitleIgnoreCase(dto.getFeatureName());
                if (existingFeature.isPresent()) {
                    // Skip creating duplicate, but continue with other files
                    continue;
                }

                // Create and save feature
                Feature feature = new Feature();
                feature.setIconImageUrl(dto.getFeatureImage());
                feature.setTitle(dto.getFeatureName());
                feature.setAltTag(dto.getFeatureAltTag());
                feature.setStatus(true);
                feature.setDescription(null);
                feature.setCreatedAt(LocalDateTime.now());
                feature.setUpdatedAt(LocalDateTime.now());
                featureRepository.save(feature);
            }
            
            response.setIsSuccess(1);
            response.setMessage("Features saved successfully...");
        } catch (Exception e) {
            // Rollback: delete all saved images on error
            for (String imageName : savedImages) {
                fileUtils.deleteFileFromDestination(imageName, featureUploadDir);
            }
            response.setMessage(e.getMessage());
        }
        
        return response;
    }
}

