package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.AmenityDto;
import com.mypropertyfact.estate.dtos.AmenityDetailedDto;
import com.mypropertyfact.estate.entities.Amenity;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.AmenityRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AmenityService {
    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Value("${upload_amenity_path}")
    private String uploadDir;

    @Value("${upload_dir}")
    private String uploadAmenityDir;

    @Autowired
    private FileUtils fileUtils;

    @Transactional
    public List<Amenity> getAllAmenities() {
        return this.amenityRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
    }

    @Transactional
    public Response postMultipleAmenities(AmenityDetailedDto dto){
        Response response = new Response();
        List<String> savedImages = new ArrayList<>();
        String amenityImage = "";
        String amenityUploadDir = uploadAmenityDir.concat("amenity/");
        try {
            if (dto.getAmenitiesFiles() == null) {
                response.setMessage("Amenities are required !");
            }
            dto.getAmenitiesFiles().forEach(file -> {
                if (!fileUtils.isTypeImage(file)) {
                    throw new IllegalArgumentException("Amenity file should be image only.");
                }
            });

            for (MultipartFile amenityFile : dto.getAmenitiesFiles()) {
                if (!fileUtils.isValidAspectRatio(amenityFile.getInputStream(), 100, 100)){
                    throw new IllegalArgumentException("Amenity image should be in 1:1 ratio or 100x100 only !");
                }
                String savedAmenityImage = fileUtils.saveDesktopImageWithResize(amenityFile, amenityUploadDir, 100, 100, 1.0f);
                savedImages.add(savedAmenityImage);
                amenityImage = savedAmenityImage;
                String originalFilename = amenityFile.getOriginalFilename();
                if(originalFilename != null && !originalFilename.isEmpty()) {
                    String fileNameWithoutExt = originalFilename.replaceFirst("[.][^.]+$", "");
                    String cleaned = fileNameWithoutExt.replace("_", " ").replaceAll("\\s+", " ").trim();
                    String amenityTitle = Arrays.stream(cleaned.split(" "))
                            .filter(word -> !word.isBlank())
                            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                            .collect(Collectors.joining(" "));
                    dto.setAmenityName(amenityTitle);
                }else{
                    dto.setAmenityName("Amenity Image");
                }
                dto.setAmenityImage(amenityImage);
                String amenityAltTag = fileUtils.generateImageAltTag(amenityFile);
                dto.setAmenityAltTag(amenityAltTag);

                Amenity amenity = new Amenity();
                amenity.setAmenityImageUrl(dto.getAmenityImage());
                amenity.setTitle(dto.getAmenityName());
                amenity.setStatus(true);
                amenity.setAltTag(dto.getAmenityAltTag());
                amenityRepository.save(amenity);
            }
            response.setIsSuccess(1);
            response.setMessage("Amenities saved successfully...");
        }catch (Exception e){
            for(String imageName: savedImages){
                fileUtils.deleteFileFromDestination(imageName, amenityUploadDir);
            }
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response postAmenity(MultipartFile file, AmenityDto amenityDto) {
        Response response = new Response();
        // Validate amenity data
        if (amenityDto.getAltTag().isEmpty() || amenityDto.getTitle().isEmpty()) {
            response.setMessage(Constants.AMENITY_TITLE_ALT_TAG_REQUIRED);
            return response;
        }
        // Validate file type
        if (file != null && !fileUtils.isTypeImage(file)) {
            response.setMessage(Constants.IMAGE_ALLOWED);
            return response;
        }
        String savedFileName = fileUtils.saveOriginalImage(file, uploadDir);
        if (amenityDto.getId() > 0) {
            Optional<Amenity> dbAmenity = this.amenityRepository.findById(amenityDto.getId());
            if (dbAmenity.isPresent()) {
                dbAmenity.get().setTitle(amenityDto.getTitle());
                dbAmenity.get().setAltTag(amenityDto.getAltTag());
                dbAmenity.get().setStatus(true);
                if (savedFileName != null && !savedFileName.isEmpty()) {
                    if(dbAmenity.get().getAmenityImageUrl() != null && !dbAmenity.get().getAmenityImageUrl().isBlank()) {
                        fileUtils.deleteFileFromDestination(dbAmenity.get().getAmenityImageUrl(), uploadDir);
                    }
                    dbAmenity.get().setAmenityImageUrl(savedFileName);
                }
                amenityRepository.save(dbAmenity.get());
            }
            response.setMessage("Amenity Updated Successfully...");
            response.setIsSuccess(1);
        } else {
            Amenity amenity = new Amenity();
            amenity.setTitle(amenityDto.getTitle());
            amenity.setAltTag(amenityDto.getAltTag());
            amenity.setAmenityImageUrl(savedFileName);
            amenity.setStatus(true);
            amenity.setCreatedAt(LocalDateTime.now());
            amenity.setUpdatedAt(LocalDateTime.now());
            this.amenityRepository.save(amenity);
            response.setMessage(Constants.AMENITY_SAVED);
            response.setIsSuccess(1);
        }
        return response;
    }

    @Transactional
    public Response deleteAmenity(int id) {
        Optional<Amenity> dbAmenity = amenityRepository.findByIdWithProjects(id); // custom method with fetch join

        if (dbAmenity.isEmpty()) {
            return new Response(0, "Amenity not found.", 0);
        }

        Amenity amenity = dbAmenity.get();

        // ⚠️ Correct way: Remove this amenity from each project's amenity list
        for (Project project : new ArrayList<>(amenity.getProjects())) {
            project.getAmenities().remove(amenity); // owning side
            projectRepository.save(project);        // persist the update
        }

        // Delete image if present
        if (amenity.getAmenityImageUrl() != null) {
            fileUtils.deleteFileFromDestination(amenity.getAmenityImageUrl(), uploadDir);
        }

        // Now safe to delete
        amenityRepository.delete(amenity);

        return new Response(1, "Amenity deleted successfully...", 0);
    }

}
