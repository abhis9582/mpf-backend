package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.ProjectGalleryDto;
import com.mypropertyfact.estate.configs.dtos.ProjectGalleryResponse;
import com.mypropertyfact.estate.entities.ProjectGallery;
import com.mypropertyfact.estate.entities.Property;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectGalleryRepository;
import com.mypropertyfact.estate.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectGalleryService {
    @Autowired
    private ProjectGalleryRepository projectGalleryRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Value("${uploads_path}")
    private String uploadDir;

    public List<ProjectGalleryResponse> getAllGalleryImages() {
        List<Object[]> projectImages = this.projectGalleryRepository.getAllGalleyImages();
        return projectImages.stream().map(item ->
                new ProjectGalleryResponse(
                        (String) item[0],
                        (String) item[1]
                )).collect(Collectors.toList());
    }

    public Response postGalleryImage(ProjectGalleryDto projectGalleryDto) {
        Response response = new Response();
        try {
            if (projectGalleryDto == null || projectGalleryDto.getImage().isEmpty()) {
                response.setMessage("Please select image");
                return response;
            }
            if (!checkContentType(projectGalleryDto.getImage())) {
                response.setMessage("File should be of type image only !");
                return response;
            }
            // Check file size < 10MB (10 * 1024 * 1024)
            if (projectGalleryDto.getImage().getSize() > 10 * 1024 * 1024) {
                response.setMessage("Gallery image size must be less than 10MB!");
                return response;
            }
            // Rename the image file (using UUID)
            String newFileName = renameFile(projectGalleryDto.getImage());
            Property property = this.propertyRepository.findById(projectGalleryDto.getProjectId()).get();
            // Save the file to the destination
            response = saveFile(projectGalleryDto.getImage(), newFileName, property, projectGalleryDto);
        } catch (Exception e) {

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

    private Response saveFile(MultipartFile file, String fileName, Property property, ProjectGalleryDto projectGalleryDto) throws IOException {
        // Determine the path to save the file
        String galleryFolder = uploadDir + property.getProjectName() + "/";
        File destinationDir = new File(galleryFolder);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }
        // Define the file path and transfer the file
        File destinationFile = new File(destinationDir, fileName);
        file.transferTo(destinationFile);
        ProjectGallery projectGallery = new ProjectGallery();
        projectGallery.setProjectId(projectGalleryDto.getProjectId());
        projectGallery.setSlugUrl(property.getSlugURL());
        projectGallery.setType("");
        projectGallery.setImage(fileName);
        this.projectGalleryRepository.save(projectGallery);
        return new Response(1, "File Uploaded successfully...");
    }
    public List<ProjectGallery> getBySlugUrl(String url){
        return this.projectGalleryRepository.findBySlugUrl(url);
    }
}