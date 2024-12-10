package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.ProjectBannerDto;
import com.mypropertyfact.estate.entities.ProjectBanner;
import com.mypropertyfact.estate.entities.Property;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectBannerRepository;
import com.mypropertyfact.estate.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectBannerService {

    @Autowired
    private ProjectBannerRepository projectBannerRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Value("${uploads_path}")
    private String uploadDir;

    public List<ProjectBanner> getAllBanners() {
        return this.projectBannerRepository.findAll();
    }
    public List<ProjectBanner> getAllDesktopBanners(){
        return this.projectBannerRepository.getAllDesktopBanners();
    }
    public List<ProjectBanner> getAllMobileBanners(){
        return this.projectBannerRepository.getAllMobileBanners();
    }

    public Response postBanner(ProjectBannerDto projectBannerDto) {
        Response response = new Response();
        try {
            // 1. Validate the input
            if (projectBannerDto == null || projectBannerDto.getProjectId() == 0) {
                response.setMessage("All fields are required!");
                return response;
            }

            // Fetch the project name from the database
            if (projectBannerDto.getProjectId() > 0) {
                Property property = this.propertyRepository.findById(projectBannerDto.getProjectId()).orElse(null);
                if (property != null) {
                    projectBannerDto.setProjectName(property.getProjectName());
                }
            }

            // 2. Validate and handle desktop banner
            if (projectBannerDto.getDesktopBanner() != null && !projectBannerDto.getDesktopBanner().isEmpty()) {
                MultipartFile desktopBannerFile = projectBannerDto.getDesktopBanner();

                // Check if the file is an image
                if (!isImageFile(desktopBannerFile)) {
                    response.setMessage("Desktop banner must be an image file!");
                    return response;
                }

                // Check file size < 10MB (10 * 1024 * 1024)
                if (desktopBannerFile.getSize() > 10 * 1024 * 1024) {
                    response.setMessage("Desktop banner image size must be less than 10MB!");
                    return response;
                }

                // Check the dimensions (e.g., width and height)
//                if (!isValidImageDimensions(desktopBannerFile, 1920, 1080)) {
//                    response.setMessage("Desktop banner image must have a resolution of 1920x1080.");
//                    return response;
//                }

                // Rename the image file (using UUID)
                String desktopNewFileName = renameFile(desktopBannerFile);

                // Save the file to the destination
                response =  saveFile(desktopBannerFile, projectBannerDto.getProjectName(), desktopNewFileName, projectBannerDto);

            }

            // 3. Validate and handle mobile banner
            if (projectBannerDto.getMobileBanner() != null && !projectBannerDto.getMobileBanner().isEmpty()) {
                MultipartFile mobileBannerFile = projectBannerDto.getMobileBanner();

                // Check if the file is an image
                if (!isImageFile(mobileBannerFile)) {
                    response.setMessage("Mobile banner must be an image file!");
                    return response;
                }

                // Check file size < 10MB (10 * 1024 * 1024)
                if (mobileBannerFile.getSize() > 10 * 1024 * 1024) {
                    response.setMessage("Mobile banner image size must be less than 10MB!");
                    return response;
                }

                // Check the dimensions (e.g., width and height)
//                if (!isValidImageDimensions(mobileBannerFile, 720, 1280)) {
//                    response.setMessage("Mobile banner image must have a resolution of 720x1280.");
//                    return response;
//                }

                // Rename the image file (using UUID)
                String mobileNewFileName = renameFile(mobileBannerFile);

                // Save the file to the destination
                response = saveFile(mobileBannerFile, projectBannerDto.getProjectName(), mobileNewFileName, projectBannerDto);
            }
        } catch (Exception e) {
            response.setMessage("Error processing banner: " + e.getMessage());
        }
        return response;
    }

    // Utility method to check image dimensions (width x height)
    private boolean isValidImageDimensions(MultipartFile file, int expectedWidth, int expectedHeight) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            return width == expectedWidth && height == expectedHeight;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Utility method to check if the file is an image
    private boolean isImageFile(MultipartFile file) {
        String mimeType = file.getContentType();
        return mimeType != null && mimeType.startsWith("image");
    }

    // Utility method to rename the image file (e.g., using UUID)
    private String renameFile(MultipartFile file) {
        String fileExtension = getFileExtension(file);
        return UUID.randomUUID().toString() + fileExtension;
    }

    // Utility method to get the file extension
    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex > 0) ? fileName.substring(dotIndex) : "";
    }

    // Utility method to save the file to the destination
    private Response saveFile(MultipartFile file, String projectName, String newFileName, ProjectBannerDto projectBannerDto) throws IOException {
        // Determine the path to save the file
        String bannerFolder = uploadDir + projectName + "/";
        File destinationDir = new File(bannerFolder);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }
        // Define the file path and transfer the file
        File destinationFile = new File(destinationDir, newFileName);
        file.transferTo(destinationFile);
        ProjectBanner projectBanner = new ProjectBanner();
        projectBanner.setProjectId(projectBannerDto.getProjectId());
        projectBanner.setDesktopBanner(newFileName);
        projectBanner.setMobileBanner(newFileName);
        projectBanner.setType(projectBannerDto.getType());
        projectBanner.setProjectName(projectBannerDto.getProjectName());
        this.projectBannerRepository.save(projectBanner);
        return new Response(1, "File Uploaded successfully...");
    }
}
