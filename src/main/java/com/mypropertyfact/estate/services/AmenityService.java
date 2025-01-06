package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.configs.dtos.AmenityDto;
import com.mypropertyfact.estate.entities.Amenity;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.AmenityRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AmenityService {
    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Value("${upload_amenity_path}")
    private String uploadDir;

    public List<Amenity> getAllAmenities() {
        return this.amenityRepository.findAll();
    }

    public Response postAmenity(MultipartFile file, AmenityDto amenityDto) {
        Response response = new Response();
        // Validate amenity data
        if (amenityDto.getAltTag().isEmpty() || amenityDto.getTitle().isEmpty()) {
            response.setMessage(Constants.AMENITY_TITLE_ALT_TAG_REQUIRED);
            return response;
        }
        // Validate file type
        if(file != null) {
            if (!file.getContentType().startsWith("image/")) {
                response.setMessage(Constants.IMAGE_ALLOWED);
                return response;
            }
        }

        try {
            // Create directory if it doesn't exist
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // Generate a unique file name (UUID)
            String newFileName = "";
            if (file != null) {
                newFileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
                // Save the file to the server
                Path path = Paths.get(dir.getPath() + "/" + newFileName);
                Files.write(path, file.getBytes());
            }
            if (amenityDto.getId() > 0) {
                Amenity dbAmenity = this.amenityRepository.findById(amenityDto.getId()).get();
                if (dbAmenity != null) {
                    dbAmenity.setTitle(amenityDto.getTitle());
                    dbAmenity.setAltTag(amenityDto.getAltTag());
                    dbAmenity.setStatus(true);
                    dbAmenity.setUpdatedAt(LocalDateTime.now());
                    if (!newFileName.isEmpty()) {
                        Path imagePath = Paths.get(uploadDir, dbAmenity.getAmenityImageUrl());
                        // Check if the image exists and delete it
                        if (Files.exists(imagePath)) {
                            Files.delete(imagePath);
                        }
                        dbAmenity.setAmenityImageUrl(newFileName);
                    }
                    this.amenityRepository.save(dbAmenity);
                }
            } else {
                Amenity amenity = new Amenity();
                amenity.setTitle(amenityDto.getTitle());
                amenity.setAltTag(amenityDto.getAltTag());
                amenity.setAmenityImageUrl(newFileName);
                amenity.setStatus(true);
                amenity.setCreatedAt(LocalDateTime.now());
                amenity.setUpdatedAt(LocalDateTime.now());
                this.amenityRepository.save(amenity);
            }
            response.setMessage(Constants.AMENITY_SAVED);
            response.setIsSuccess(1);
        } catch (Exception e) {
            response.setMessage(Constants.SOMETHING_WENT_WRONG);
        }
        return response;
    }

    public Response deleteAmenity(int id) {
        Response response = new Response();
        try {
            Amenity amenity = this.amenityRepository.findById(id).get();
            Path imagePath = Paths.get(uploadDir, amenity.getAmenityImageUrl());
            // Check if the image exists and delete it
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
            }
            this.amenityRepository.deleteById(id);
            response.setMessage("Data deleted successfully...");
            response.setIsSuccess(1);
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
