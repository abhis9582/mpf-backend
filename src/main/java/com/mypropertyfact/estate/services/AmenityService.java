package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.AmenityDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AmenityService {
    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Value("${upload_amenity_path}")
    private String uploadDir;

    @Autowired
    private FileUtils fileUtils;

    @Transactional
    public List<Amenity> getAllAmenities() {
        return this.amenityRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
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
