package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.dtos.NearbyBenefitDetailedDto;
import com.mypropertyfact.estate.entities.MasterBenefit;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.MasterBenefitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NearbyBenefitService {
    
    @Autowired
    private MasterBenefitRepository masterBenefitRepository;
    
    @Autowired
    private FileUtils fileUtils;
    
    @Value("${upload_dir}")
    private String uploadDir;
    
    @Transactional
    public List<MasterBenefit> getAllNearbyBenefits() {
        return masterBenefitRepository.findAll();
    }
    
    @Transactional
    public Response postMultipleNearbyBenefits(NearbyBenefitDetailedDto dto) {
        Response response = new Response();
        List<String> savedImages = new ArrayList<>();
        String benefitImage = "";
        String benefitUploadDir = uploadDir.concat("nearby-benefit/");
        
        try {
            if (dto.getNearbyBenefitsFiles() == null || dto.getNearbyBenefitsFiles().isEmpty()) {
                response.setMessage("Nearby benefits are required !");
                response.setIsSuccess(0);
                return response;
            }
            
            // Validate all files are images
            dto.getNearbyBenefitsFiles().forEach(file -> {
                if (!fileUtils.isTypeImage(file)) {
                    throw new IllegalArgumentException("Nearby benefit file should be image only.");
                }
            });

            for (MultipartFile benefitFile : dto.getNearbyBenefitsFiles()) {
                // Validate aspect ratio (1:1 ratio for icons, similar to features)
                if (!fileUtils.isValidAspectRatio(benefitFile.getInputStream(), 100, 100)) {
                    throw new IllegalArgumentException("Nearby benefit image should be in 1:1 ratio or 100x100 only !");
                }
                
                // Save and resize the image
                String savedBenefitImage = fileUtils.saveDesktopImageWithResize(benefitFile, benefitUploadDir, 100, 100, 1.0f);
                savedImages.add(savedBenefitImage);
                benefitImage = savedBenefitImage;
                
                // Generate title from filename
                String originalFilename = benefitFile.getOriginalFilename();
                String benefitTitle;
                if (originalFilename != null && !originalFilename.isEmpty()) {
                    String fileNameWithoutExt = originalFilename.replaceFirst("[.][^.]+$", "");
                    String cleaned = fileNameWithoutExt.replace("_", " ").replaceAll("\\s+", " ").trim();
                    benefitTitle = Arrays.stream(cleaned.split(" "))
                            .filter(word -> !word.isBlank())
                            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                            .collect(Collectors.joining(" "));
                    dto.setBenefitName(benefitTitle);
                } else {
                    dto.setBenefitName("Nearby Benefit Image");
                }
                
                dto.setBenefitImage(benefitImage);
                
                // Generate alt tag from filename
                String benefitAltTag = fileUtils.generateImageAltTag(benefitFile);
                dto.setBenefitAltTag(benefitAltTag);

                // Check if benefit with this name already exists
                Optional<MasterBenefit> existingBenefit = masterBenefitRepository.findByBenefitName(dto.getBenefitName());
                if (existingBenefit.isPresent()) {
                    // Skip creating duplicate, but continue with other files
                    continue;
                }

                // Create and save benefit
                MasterBenefit benefit = new MasterBenefit();
                benefit.setBenefitIcon(dto.getBenefitImage());
                benefit.setBenefitName(dto.getBenefitName());
                benefit.setAltTag(dto.getBenefitAltTag());
                masterBenefitRepository.save(benefit);
            }
            
            response.setIsSuccess(1);
            response.setMessage("Nearby benefits saved successfully...");
        } catch (Exception e) {
            // Rollback: delete all saved images on error
            for (String imageName : savedImages) {
                fileUtils.deleteFileFromDestination(imageName, benefitUploadDir);
            }
            response.setMessage(e.getMessage());
        }
        
        return response;
    }
    
    @Transactional
    public Response deleteNearbyBenefit(Integer id) {
        Optional<MasterBenefit> dbBenefit = masterBenefitRepository.findById(id);
        
        if (dbBenefit.isEmpty()) {
            return new Response(0, "Nearby benefit not found.", 0);
        }
        
        MasterBenefit benefit = dbBenefit.get();
        
        // Delete image if present
        if (benefit.getBenefitIcon() != null && !benefit.getBenefitIcon().isEmpty()) {
            String benefitUploadDir = uploadDir.concat("nearby-benefit/");
            fileUtils.deleteFileFromDestination(benefit.getBenefitIcon(), benefitUploadDir);
        }
        
        masterBenefitRepository.delete(benefit);
        return new Response(1, "Nearby benefit deleted successfully!", 0);
    }
}
