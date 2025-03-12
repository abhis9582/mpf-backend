package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.models.ProjectDto;
import com.mypropertyfact.estate.models.Response;
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

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Value("${uploads_path}")
    private String uploadDir;

    public List<Project> getAllProjects() {
        return this.projectRepository.findAll();
    }

    public Project getBySlugUrl(String url) {
        return this.projectRepository.findBySlugURL(url);
    }

    public List<Project> getAllBuilderProjects(int id) {
        return this.projectRepository.getAllBuilderProjects(id);
    }

    public Response deleteProject(int id) {
        Response response = new Response();
        try {
            if (id > 0) {
                Project project = projectRepository.findById(id).orElse(null);
                if(project != null){
                    String dirPath = uploadDir + project.getSlugURL();
                    // Delete the entire directory after files are removed
                    deleteDirectory(dirPath);
                    projectRepository.delete(project);
                    response.setMessage("Project deleted successfully...");
                    response.setIsSuccess(1);
                }else{
                    response.setMessage("no project found !");
                }
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }
    private void deleteDirectory(String dirPath) {
        File directory = new File(dirPath);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete(); // Delete each file in the directory
                }
            }
            directory.delete(); // Delete the empty directory
        }
    }
    public Response saveProject(MultipartFile projectLogo,
                                MultipartFile locationMap,
                                MultipartFile projectThumbnail,
                                ProjectDto projectDto) {
        Response response = new Response();
        try {
            Project project = projectDto.getId() != 0 ? projectRepository.findById(projectDto.getId()).orElse(new Project())
                    : new Project();

            // Generate slug URL if empty
            if (projectDto.getSlugURL().isEmpty() && projectDto.getProjectName() != null) {
                projectDto.setSlugURL(generateSlug(projectDto.getProjectName()));
            }

            // Generating path for storing image
            String projectDir = uploadDir + projectDto.getSlugURL().toLowerCase().trim();
            createDirectory(projectDir);

            // Process images only if new files are provided
            if (projectLogo != null) {
                deleteExistingFile(projectDir, project.getProjectLogo());
                project.setProjectLogo(processFile(projectLogo, projectDir));
            }

            if (locationMap != null) {
                deleteExistingFile(projectDir, project.getLocationMap());
                project.setLocationMap(processFile(locationMap, projectDir));
            }

            if (projectThumbnail != null) {
                deleteExistingFile(projectDir, project.getProjectThumbnail());
                project.setProjectThumbnail(processFile(projectThumbnail, projectDir));
            }

            //save data to database
            mapDtoToEntity(project, projectDto);
            projectRepository.save(project);

            response.setMessage(Constants.PROJECT_SAVED);
            response.setIsSuccess(1);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // deleting existing file
    private void deleteExistingFile(String dirPath, String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            File oldFile = new File(dirPath, fileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }
    }

    //Function for creating directory
    private void createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Generating file name and saving it to upload directory
    private String processFile(MultipartFile file, String uploadDir) throws Exception {
        if (file != null && file.getContentType().startsWith("image/")) {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "." + extension;
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath);
            return fileName;
        }
        return "";
    }

    //generating slug url
    private String generateSlug(String projectName) {
        return String.join("-", projectName.trim().split("\\s+")).toLowerCase();
    }

    // Map DTO fields to Project entity
    private void mapDtoToEntity(Project project, ProjectDto dto) {
        project.setMetaTitle(dto.getMetaTitle());
        project.setMetaDescription(dto.getMetaDescription());
        project.setMetaKeyword(dto.getMetaKeyword());
        project.setProjectName(dto.getProjectName());
        project.setProjectAddress(dto.getProjectAddress());
        project.setState(dto.getState());
        project.setCityLocation(dto.getCityLocation());
        project.setProjectLocality(dto.getProjectLocality());
        project.setProjectConfiguration(dto.getProjectConfiguration());
        project.setProjectBy(dto.getProjectBy());
        project.setProjectPrice(dto.getProjectPrice());
        project.setIvrNo(dto.getIvrNo());
        project.setReraNo(dto.getReraNo());
        project.setReraWebsite(dto.getReraWebsite());
        project.setProjectStatus(dto.getProjectStatus());
        project.setPropertyType(dto.getPropertyType());
        project.setSlugURL(dto.getSlugURL());
        project.setShowFeaturedProperties(true);
        project.setCountry(dto.getCountry());
        project.setAmenityDesc(dto.getAmenityDesc());
        project.setLocationDesc(dto.getLocationDesc());
        project.setFloorPlanDesc(dto.getFloorPlanDesc());
        project.setStatus(true);
    }

    public List<Project> searchByPropertyTypeLocationBudget(String propertyType, String propertyLocation, String budget) {
        int start = 0;
        int end = 0;
        if(budget.equals("Up to 1Cr*") ){
            start = 0;
            end = 1;
        } else if (budget.equals("1-3 Cr*")) {
            start = 1;
            end = 3;
        }else if (budget.equals("3-5 Cr*")){
            start = 3;
            end = 5;
        }else if (budget.equals("Above 5 Cr*")){
            start = 5;
            end = 10;
        }
        return projectRepository.searchByPropertyTypeLocationBudget(propertyType, propertyLocation,
                start, end);
    }
}
