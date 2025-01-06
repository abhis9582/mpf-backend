package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.LocationBenefitDto;
import com.mypropertyfact.estate.entities.LocationBenefit;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.LocationBenefitRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LocationBenefitService {
    @Autowired
    private LocationBenefitRepository locationBenefitRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Value("${upload_icon_path}")
    private String uploadDir;

    public List<LocationBenefitDto> getAllBenefits() {
        List<Object[]> response = this.locationBenefitRepository.getAllWithProjectName();
        return response.stream().map(item -> new LocationBenefitDto(
                (int) item[0],
                (int) item[1],
                (String) item[2],
                (String) item[3],
                (String) item[4],
                (String) item[5]
        )).collect(Collectors.toList());
    }

    public Response addUpdateBenefit(MultipartFile file, LocationBenefitDto locationBenefitDto) {
        Response response = new Response();
        try {
            if (locationBenefitDto == null) {
                response.setMessage("All fields are required !");
                return response;
            }
            String iconImageName = "";
            File destinationDir = new File(uploadDir);
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }
            Project project = this.projectRepository.findById(locationBenefitDto.getProjectId()).get();
            if(project != null) {
                locationBenefitDto.setSlugUrl(project.getSlugURL());
            }
            if (file != null) {
                if (!file.getContentType().startsWith("image/")) {
                    response.setMessage("Only image is allowed !");
                    return response;
                }
                if (file.getSize() > 10 * 1024 * 1024) {
                    response.setMessage("Image should be < 10MB !");
                    return response;
                }
                iconImageName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
                File destinationFile = new File(destinationDir, iconImageName);
                file.transferTo(destinationFile);
            }
            if (locationBenefitDto.getId() > 0) {
                LocationBenefit locationBenefit = this.locationBenefitRepository.findById(locationBenefitDto.getId()).get();
                if(locationBenefit != null){
                    if(!iconImageName.isEmpty()) {
                        Path imagePath = Paths.get(uploadDir, locationBenefit.getIconImage());
                        if(Files.exists(imagePath)){
                            Files.delete(imagePath);
                        }
                        locationBenefit.setIconImage(iconImageName);
                    }
                }
                locationBenefit.setBenefitName(locationBenefitDto.getBenefitName());
                locationBenefit.setDistance(locationBenefitDto.getDistance());
                locationBenefit.setSlugUrl(locationBenefitDto.getSlugUrl());
                locationBenefit.setProjectId(locationBenefitDto.getProjectId());
                this.locationBenefitRepository.save(locationBenefit);
                response.setIsSuccess(1);
                response.setMessage("Location benefit updated successfully...");
            } else {
                LocationBenefit locationBenefit = new LocationBenefit();
                locationBenefit.setBenefitName(locationBenefitDto.getBenefitName());
                locationBenefit.setDistance(locationBenefitDto.getDistance());
                locationBenefit.setSlugUrl(locationBenefitDto.getSlugUrl());
                locationBenefit.setIconImage(iconImageName);
                locationBenefit.setProjectId(locationBenefitDto.getProjectId());
                this.locationBenefitRepository.save(locationBenefit);
                response.setMessage("Location benefit added successfully...");
                response.setIsSuccess(1);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<LocationBenefit> getBySlug(String url) {
        return this.locationBenefitRepository.findBySlugUrl(url);
    }

    public Response deleteLocationBenefit(int id) {
        Response response = new Response();
        try {
            LocationBenefit benefit = this.locationBenefitRepository.findById(id).get();
            if (benefit != null) {
                Path imagePath = Paths.get(uploadDir, benefit.getIconImage());
                if (Files.exists(imagePath)) {
                    Files.delete(imagePath);
                }
            }
            this.locationBenefitRepository.deleteById(id);
            response.setMessage("Data deleted successfully...");
            response.setIsSuccess(1);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
