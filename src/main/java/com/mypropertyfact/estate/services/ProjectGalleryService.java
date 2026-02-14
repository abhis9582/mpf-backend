package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.dtos.ProjectGalleryDto;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectGallery;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectGalleryRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectGalleryService {

    private final ProjectGalleryRepository projectGalleryRepository;

    private final ProjectRepository projectRepository;

    @Value("${uploads_path}")
    private String uploadDir;

    private final FileUtils fileUtils;

    public List<Map<String, Object>> getAllGalleryImages() {
        List<ProjectGallery> galleryImages = this.projectGalleryRepository.findAll();
        Map<Integer, Map<String, Object>> resultObj = new HashMap<>();

        for(ProjectGallery galleryImage: galleryImages){
            int projectId = galleryImage.getProject().getId();

            Map<String, Object> projectData = resultObj.computeIfAbsent(projectId, id->{
                Map<String, Object> obj = new HashMap<>();
                obj.put("projectId", id);
                obj.put("pName", galleryImage.getProject().getProjectName());
                obj.put("slugURL", galleryImage.getProject().getSlugURL());
                obj.put("galleryImage", new ArrayList<Map<String, Object>>());
                return obj;
            });

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> galleryList = (List<Map<String, Object>>) projectData.get("galleryImage");

            Map<String, Object> galleryData = new HashMap<>();
            galleryData.put("image", galleryImage.getImage());
            galleryData.put("id", galleryImage.getId());
            galleryList.add(galleryData);
        }
        return new ArrayList<>(resultObj.values());
    }

    public Response postGalleryImage(ProjectGalleryDto projectGalleryDto) {
        Response response = new Response();
        try {
            Optional<Project> project;
            if(projectGalleryDto.getProjectId() != 0) {
             project = projectRepository.findById(projectGalleryDto.getProjectId());
            } else {
                project = Optional.empty();
            }
            if(projectGalleryDto.getGalleryImageList() != null) {
                for (MultipartFile galleryImage: projectGalleryDto.getGalleryImageList()) {
                    if (!fileUtils.isTypeImage(galleryImage)) {
                        response.setMessage("File should be of type image only !");
                        return response;
                    }
                    // Check file size < 10MB (10 * 1024 * 1024)
                    if (galleryImage.getSize() > 10 * 1024 * 1024) {
                        response.setMessage("Gallery image size must be less than 10MB!");
                        return response;
                    }
                    // Rename the image file (using UUID)
                    String newFileName = renameFile(galleryImage);
                    Project projectObj = new Project();
                    if (project.isPresent()) {
                        projectObj = project.get();
                    }
                    // Save the file to the destination
                    saveFile(galleryImage, newFileName, projectObj, projectGalleryDto);
                }
            }
            if(projectGalleryDto.getDeletedImageIds() != null) {
                for(Integer galleryImageId: projectGalleryDto.getDeletedImageIds()) {
                    Optional<ProjectGallery> galleryData = projectGalleryRepository.findById(galleryImageId);
                    galleryData.ifPresent(gallery-> {
                        if(project.isPresent()) {
                            String destination = uploadDir + project.get().getSlugURL() + "/";
                            fileUtils.deleteFileFromDestination(gallery.getImage(), destination);
                        }
                    });
                }
                projectGalleryRepository.deleteAllById(projectGalleryDto.getDeletedImageIds());
            }
            response.setMessage("Gallery images uploaded successfully...");
            response.setIsSuccess(1);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public boolean checkContentType(MultipartFile multipartFile) {
        String mimeType = multipartFile.getContentType();
        return mimeType != null && mimeType.startsWith("image");
    }

    // Utility method to get the file extension
    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex > 0) ? fileName.substring(dotIndex) : "";
    }

    private String renameFile(MultipartFile file) {
        String fileExtension = getFileExtension(file);
        return UUID.randomUUID().toString() + fileExtension;
    }

    private Response saveFile(MultipartFile file, String fileName, Project project, ProjectGalleryDto projectGalleryDto) throws IOException {
        // Determine the path to save the file
        String galleryFolder = uploadDir + project.getSlugURL() + "/";
        File destinationDir = new File(galleryFolder);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }
        // Define the file path and transfer the file
        File destinationFile = new File(destinationDir, fileName);
        file.transferTo(destinationFile);
        ProjectGallery projectGallery = new ProjectGallery();
        projectGallery.setSlugUrl(project.getSlugURL());
        projectGallery.setType("");
        projectGallery.setImage(fileName);
        projectGallery.setAltTag(fileName);
        projectGallery.setProject(project);
        this.projectGalleryRepository.save(projectGallery);
        return new Response(1, "File Uploaded successfully...", 0);
    }
    public List<ProjectGallery> getBySlugUrl(String url){
        return this.projectGalleryRepository.findBySlugUrl(url);
    }

    public Response deleteGalleryImage(int id){
        Response response = new Response();
        try{
            projectGalleryRepository.findById(id).ifPresent(p->{
                projectGalleryRepository.deleteById(id);
            });
            response.setMessage("Gallery image deleted successfully...");
            response.setIsSuccess(1);
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
