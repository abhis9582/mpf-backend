package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.configs.dtos.AmenityDto;
import com.mypropertyfact.estate.entities.Amenity;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectAmenity;
import com.mypropertyfact.estate.models.ProjectAmenityDto;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.AmenityRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.stream.Collectors;

@Service
public class AmenityService {
    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Value("${upload_amenity_path}")
    private String uploadDir;
    public List<Amenity> getAllAmenities(){
        return this.amenityRepository.findAll();
    }

    public Response postAmenity(AmenityDto amenityDto){
        Response response = new Response();
        if(amenityDto.getAmenityImage()== null || amenityDto.getAmenityImage().isEmpty()){
            response.setMessage(Constants.IMAGE_REQUIRED);
            return response;
        }
        // Validate amenity data
        if (amenityDto.getAltTag().isEmpty() || amenityDto.getTitle().isEmpty()) {
            response.setMessage(Constants.AMENITY_TITLE_ALT_TAG_REQUIRED);
            return response;
        }
        // Validate file type
        MultipartFile amenityImage = amenityDto.getAmenityImage();
        String contentType = amenityImage.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            response.setMessage(Constants.IMAGE_ALLOWED);
            return response;
        }
        try {
            // Create directory if it doesn't exist
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generate a unique file name (UUID)
            String fileExtension = StringUtils.getFilenameExtension(amenityImage.getOriginalFilename());
            String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // Save the file to the server
            Path path = Paths.get(dir.getPath() + "/" + newFileName);
            Files.write(path, amenityImage.getBytes());
            Amenity amenity = new Amenity();
            amenity.setTitle(amenityDto.getTitle());
            amenity.setAltTag(amenityDto.getAltTag());
            amenity.setAmenityImageUrl(newFileName);
            amenity.setStatus(true);
            amenity.setCreatedAt(LocalDateTime.now());
            amenity.setUpdatedAt(LocalDateTime.now());
            this.amenityRepository.save(amenity);
            response.setMessage(Constants.AMENITY_SAVED);
            response.setIsSuccess(1);
        }catch (Exception e){
            response.setMessage(Constants.SOMETHING_WENT_WRONG);
        }
        return response;
    }

    public void deleteAmenity(int id){
        this.amenityRepository.deleteById(id);
    }
}
