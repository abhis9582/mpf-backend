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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class LocationBenefitService {
    @Autowired
    private LocationBenefitRepository locationBenefitRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Value("${uploads_path}")
    private String uploadDir;

    public List<Map<String, Object>> getAllBenefits() {
        List<LocationBenefit> locationBenefits = locationBenefitRepository.findAll();

        Map<Integer, Map<String, Object>> result = new HashMap<>();
        for (LocationBenefit locationBenefit : locationBenefits) {
            int projectId = locationBenefit.getProject().getId();
            Map<String, Object> projectLocationBenefitObj = result.computeIfAbsent(projectId, id -> {
                Map<String, Object> projectObj = new HashMap<>();
                projectObj.put("projectId", id);
                projectObj.put("projectName", locationBenefit.getProject().getProjectName());
                projectObj.put("slugUrl", locationBenefit.getProject().getSlugURL());
                projectObj.put("locationBenefits", new ArrayList<>());
                return projectObj;
            });

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> locationBenefitList = (List<Map<String, Object>>) projectLocationBenefitObj.get("locationBenefits");
            Map<String, Object> locationObj = new HashMap<>();
            locationObj.put("benefitName", locationBenefit.getBenefitName());
            locationObj.put("distance", locationBenefit.getDistance());
            locationObj.put("id", locationBenefit.getId());
            locationObj.put("image", locationBenefit.getIconImage());
            locationBenefitList.add(locationObj);
        }

        return new ArrayList<>(result.values());
    }

    public Response addUpdateBenefit(MultipartFile file, LocationBenefitDto locationBenefitDto) {
        Response response = new Response();
        if (locationBenefitDto == null) {
            response.setMessage("All fields are required !");
            return response;
        }
        Optional<Project> project = this.projectRepository.findById(locationBenefitDto.getProjectId());
        try {
            String iconImageName = "";
            String dir = "";
            if (project.isPresent()) {
                dir = uploadDir + project.get().getSlugURL()+ "/";
            }
            File destinationDir = new File(dir);
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
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
                Optional<LocationBenefit> locationBenefit = this.locationBenefitRepository.findById(locationBenefitDto.getId());
                if (locationBenefit.isPresent()) {
                    LocationBenefit benefit = locationBenefit.get();
                    if (!iconImageName.isEmpty()) {
                        Path imagePath = Paths.get(uploadDir, benefit.getIconImage());
                        try {
                            if (Files.exists(imagePath)) {
                                Files.delete(imagePath);
                            }
                        } catch (IOException e) {
                            e.printStackTrace(); // Or handle logging more gracefully
                        }
                        benefit.setIconImage(iconImageName);
                    }
                    benefit.setBenefitName(locationBenefitDto.getBenefitName());
                    benefit.setDistance(locationBenefitDto.getDistance());
                    project.ifPresent(benefit::setProject);
                    locationBenefitRepository.save(benefit);
                    response.setIsSuccess(1);
                    response.setMessage("Location benefit updated successfully...");
                }
            } else {
                LocationBenefit locationBenefit = new LocationBenefit();
                locationBenefit.setBenefitName(locationBenefitDto.getBenefitName());
                locationBenefit.setDistance(locationBenefitDto.getDistance());
                locationBenefit.setIconImage(iconImageName);
                project.ifPresent(locationBenefit::setProject);
                this.locationBenefitRepository.save(locationBenefit);
                response.setMessage("Location benefit added successfully...");
                response.setIsSuccess(1);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
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
