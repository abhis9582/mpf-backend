package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.ProjectBannerDto;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectBanner;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectBannerRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
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
    private ProjectRepository projectRepository;

    @Value("${uploads_path}")
    private String uploadDir;

    public List<ProjectBanner> getAllBanners() {
        return this.projectBannerRepository.findAll();
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
                Project project = this.projectRepository.findById(projectBannerDto.getProjectId()).orElse(null);
                if (project != null) {
                    projectBannerDto.setSlugURL(project.getSlugURL());
                    projectBannerDto.setProjectName(project.getProjectName());
                }
            }

            // 2. Validate and handle desktop banner
            if (projectBannerDto.getDesktopBanner() != null && !projectBannerDto.getDesktopBanner().isEmpty() ||
                    projectBannerDto.getMobileBanner() != null && projectBannerDto.getMobileBanner().isEmpty()) {
                MultipartFile desktopBannerFile = projectBannerDto.getDesktopBanner();
                MultipartFile mobileBannerFile = projectBannerDto.getMobileBanner();
                // Check if the file is an image
                if (!isImageFile(desktopBannerFile) || !isImageFile(mobileBannerFile)) {
                    response.setMessage("Desktop banner must be an image file!");
                    return response;
                }

                // Check file size < 10MB (10 * 1024 * 1024)
                if (desktopBannerFile.getSize() > 10 * 1024 * 1024 || mobileBannerFile.getSize() > 10 * 1024 * 1024) {
                    response.setMessage("Desktop or mobile banner image size must be less than 10MB!");
                    return response;
                }

                // Check the dimensions (e.g., width and height)
//                if (!isValidImageDimensions(desktopBannerFile, 1920, 1080)) {
//                    response.setMessage("Desktop banner image must have a resolution of 1920x1080.");
//                    return response;
//                }
                // Rename the image file (using UUID)
                String desktopNewFileName = renameFile(desktopBannerFile);
                String mobileNewFIleName = renameFile(mobileBannerFile);
                // Save the file to the destination
                response = saveFile(desktopBannerFile, projectBannerDto.getSlugURL(), desktopNewFileName, projectBannerDto,
                        mobileNewFIleName, mobileBannerFile);
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
    private Response saveFile(MultipartFile file, String projectName, String newFileName, ProjectBannerDto projectBannerDto,
                              String mobileBannerFileName, MultipartFile mobileBanner) throws IOException {
        // Determine the path to save the file
        String bannerFolder = uploadDir + projectName + "/";
        File destinationDir = new File(bannerFolder);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }
        if(projectBannerDto.getId() > 0){
            // Getting data of banners from Id
            ProjectBanner existingData = this.projectBannerRepository.findById(projectBannerDto.getId()).get();
            // Checking if data exists
            if(existingData != null){
                // Handle desktop banner image upload (delete old image )
                if (mobileBanner != null && !mobileBanner.isEmpty() ||
                !file.isEmpty() && file != null) {
                    // Delete old desktop banner image
                    if (!existingData.getDesktopBanner().isEmpty() ||
                            !existingData.getMobileBanner().isEmpty()) {
                        File oldDesktopFile = new File(bannerFolder, existingData.getDesktopBanner());
                        File oldMobileFile = new File(bannerFolder, existingData.getMobileBanner());
                        if (oldDesktopFile.exists()) {
                            oldDesktopFile.delete();
                            oldMobileFile.delete();
                        }
                    }
                    // Save new desktop banner file
                    File destinationFile = new File(destinationDir, newFileName);
                    file.transferTo(destinationFile);
                    File destinationMobileFile = new File(destinationDir, mobileBannerFileName);
                    mobileBanner.transferTo(destinationMobileFile);
                    existingData.setProjectId(projectBannerDto.getProjectId());
                    existingData.setDesktopBanner(newFileName);
                    existingData.setMobileBanner(mobileBannerFileName);
                    existingData.setProjectName(projectBannerDto.getProjectName());
                    existingData.setSlugURL(projectBannerDto.getSlugURL());
                    existingData.setAltTag(projectBannerDto.getAltTag());
                    this.projectBannerRepository.save(existingData);
                }
            }
        }else{
            // Define the file path and transfer the file
            File destinationFile = new File(destinationDir, newFileName);
            File destinationMobileFile = new File(destinationDir, mobileBannerFileName);
            file.transferTo(destinationFile);
            mobileBanner.transferTo(destinationMobileFile);
            ProjectBanner projectBanner = new ProjectBanner();
            projectBanner.setProjectId(projectBannerDto.getProjectId());
            projectBanner.setDesktopBanner(newFileName);
            projectBanner.setMobileBanner(mobileBannerFileName);
            projectBanner.setProjectName(projectBannerDto.getProjectName());
            projectBanner.setSlugURL(projectBannerDto.getSlugURL());
            projectBanner.setAltTag(projectBannerDto.getAltTag());
            this.projectBannerRepository.save(projectBanner);
        }
        return new Response(1, "File Uploaded successfully...");
    }

    public ProjectBanner getBySlug(String url) {
        return this.projectBannerRepository.getBySlug(url);
    }
}
