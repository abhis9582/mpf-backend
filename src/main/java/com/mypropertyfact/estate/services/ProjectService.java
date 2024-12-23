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

    public Response saveProject(ProjectDto projectDto) {
        Response response = new Response();
        try {
            // checking all images
            if (projectDto.getLocationMap().isEmpty() || projectDto.getProjectLogo().isEmpty() || projectDto.getProjectThumbnail().isEmpty()) {
                response.setMessage("All images required !");
                return response;
            }
            // checking null and project name validation
            if (projectDto == null || projectDto.getProjectName().isEmpty()) {
                response.setMessage("Project Name is required");
                return response;
            }
            // checking content type
            if (!projectDto.getLocationMap().getContentType().startsWith("image/") ||
                    !projectDto.getProjectLogo().getContentType().startsWith("image/")
                    || !projectDto.getProjectThumbnail().getContentType().startsWith("image/")) {
                response.setMessage("Only type image is acceptable");
                return response;
            }
            // checking if slugURL is empty then create new url
            if (projectDto.getSlugURL().isEmpty()) {
                // creating slug url by using name
                String[] slugURL = projectDto.getProjectName().split(" ");
                String resultUrl = slugURL[0];
                for (int i = 1; i < slugURL.length; i++) {
                    resultUrl += "-" + slugURL[i];
                }
                projectDto.setSlugURL(resultUrl.toLowerCase());
            }
            // Generate location Map image name (UUID)
            String locationMapExtension = StringUtils.getFilenameExtension(projectDto.getLocationMap().getOriginalFilename());
            String locationMapImageName = UUID.randomUUID().toString() + "." + locationMapExtension;

            // Generate project logo name (UUID)
            String projectLogoExtension = StringUtils.getFilenameExtension(projectDto.getLocationMap().getOriginalFilename());
            String projectLogoName = UUID.randomUUID().toString() + "." + projectLogoExtension;

            // Generate project thumbnail name (UUID)
            String projectThumbnailExtension = StringUtils.getFilenameExtension(projectDto.getLocationMap().getOriginalFilename());
            String projectThumbnailName = UUID.randomUUID().toString() + "." + projectThumbnailExtension;

            // Create directory if it doesn't exist
            File dir = new File(uploadDir + projectDto.getSlugURL().toLowerCase().trim());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Save the file to the server
            Path path = Paths.get(dir.getPath() + "/" + locationMapImageName);
            Path logoPath = Paths.get(dir.getPath() + "/" + projectLogoName);
            Path thumbNaliPath = Paths.get(dir.getPath() + "/" + projectThumbnailName);

            Files.write(path, projectDto.getLocationMap().getBytes());
            Files.write(logoPath, projectDto.getProjectLogo().getBytes());
            Files.write(thumbNaliPath, projectDto.getProjectThumbnail().getBytes());

            Project project = new Project();
            project.setMetaTitle(projectDto.getMetaTitle());
            project.setMetaDescription(projectDto.getMetaDescription());
            project.setMetaKeyword(projectDto.getMetaKeyword());
            project.setProjectName(projectDto.getProjectName());
            project.setProjectAddress(projectDto.getProjectAddress());
            project.setState(projectDto.getState());
            project.setCityLocation(projectDto.getCityLocation());
            project.setProjectLocality(projectDto.getProjectLocality());
            project.setProjectConfiguration(projectDto.getProjectConfiguration());
            project.setProjectBy(projectDto.getProjectBy());
            project.setProjectPrice(projectDto.getProjectPrice());
            project.setIvrNo(projectDto.getIvrNo());
            project.setLocationMap(locationMapImageName);
            project.setReraNo(projectDto.getReraNo());
            project.setReraWebsite(projectDto.getReraWebsite());
            project.setProjectStatus(projectDto.getProjectStatus());
            project.setProjectLogo(projectLogoName);
            project.setProjectThumbnail(projectThumbnailName);
            project.setPropertyType(projectDto.getPropertyType());
            project.setSlugURL(projectDto.getSlugURL());
            project.setShowFeaturedProperties(true);
            project.setStatus(true);
            this.projectRepository.save(project);
            response.setMessage(Constants.PROJECT_SAVED);
            response.setIsSuccess(1);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Project> getAllBuilderProjects(int id){
        return this.projectRepository.getAllBuilderProjects(id);
    }
}
