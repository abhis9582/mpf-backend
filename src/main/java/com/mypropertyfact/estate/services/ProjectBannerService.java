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
import java.util.Optional;
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

    public Response postBanner(MultipartFile mobileBanner, MultipartFile desktopBanner, ProjectBannerDto projectBannerDto) {
        Response response = new Response();
        try {
            // 1. Validate the input
            if (projectBannerDto == null || projectBannerDto.getProjectId() == 0) {
                response.setMessage("All fields are required!");
                return response;
            }

            // Fetch the project name and slug url from the database
            Optional<Project> project = this.projectRepository.findById(projectBannerDto.getProjectId());
            project.ifPresent(p -> {
                projectBannerDto.setSlugURL(p.getSlugURL());
                projectBannerDto.setProjectName(p.getProjectName());
            });
            String desktopNewFileName = "";
            String mobileNewFIleName = "";
            if (mobileBanner != null) {
                // Check if the file is an image
                if (!isImageFile(mobileBanner)) {
                    response.setMessage("Banner must be an image file!");
                    return response;
                }
                // Check file size < 10MB (10 * 1024 * 1024)
                if (mobileBanner.getSize() > 10 * 1024 * 1024) {
                    response.setMessage("Desktop or mobile banner image size must be less than 10MB!");
                    return response;
                }
                mobileNewFIleName = renameFile(mobileBanner);
            }
            if (desktopBanner != null) {
                // Check if the file is an image
                if (!isImageFile(desktopBanner)) {
                    response.setMessage("Banner must be an image file!");
                    return response;
                }
                // Check file size < 10MB (10 * 1024 * 1024)
                if (desktopBanner.getSize() > 10 * 1024 * 1024) {
                    response.setMessage("Desktop or mobile banner image size must be less than 10MB!");
                    return response;
                }
                desktopNewFileName = renameFile(desktopBanner);
            }
            // Save the file to the destination
            response = saveFile(mobileBanner, desktopBanner, projectBannerDto, mobileNewFIleName, desktopNewFileName);
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
    //mobileBanner, desktopBanner,  projectBannerDto, desktopNewFileName, mobileNewFIleName
    private Response saveFile(MultipartFile mobileBanner, MultipartFile desktopBanner, ProjectBannerDto projectBannerDto,
                              String mobileNewFileName, String desktopNewFileName) throws IOException {
        // Determine the path to save the file
        String bannerFolder = uploadDir + projectBannerDto.getSlugURL() + "/";
        File destinationDir = new File(bannerFolder);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        if (projectBannerDto.getId() > 0) {
            // Getting existing banner data
            Optional<ProjectBanner> existingData = this.projectBannerRepository.findById(projectBannerDto.getId());

            // Update existing record if found
            existingData.ifPresent(p -> {
                try {
                    // Delete and update banners only if new banners are uploaded
                    if (mobileBanner != null && !mobileBanner.isEmpty()) {
                        File oldMobileFile = new File(bannerFolder, p.getMobileBanner());
                        if (oldMobileFile.exists()) oldMobileFile.delete();

                        File mobileBannerFile = new File(destinationDir, mobileNewFileName);
                        mobileBanner.transferTo(mobileBannerFile);
                        p.setMobileBanner(mobileNewFileName);
                    }

                    if (desktopBanner != null && !desktopBanner.isEmpty()) {
                        File oldDesktopFile = new File(bannerFolder, p.getDesktopBanner());
                        if (oldDesktopFile.exists()) oldDesktopFile.delete();

                        File desktopBannerFile = new File(destinationDir, desktopNewFileName);
                        desktopBanner.transferTo(desktopBannerFile);
                        p.setDesktopBanner(desktopNewFileName);
                    }

                    // Update other fields
                    p.setProjectId(projectBannerDto.getProjectId());
                    p.setProjectName(projectBannerDto.getProjectName());
                    p.setSlugURL(projectBannerDto.getSlugURL());
                    p.setAltTag(projectBannerDto.getAltTag());

                    this.projectBannerRepository.save(p);
                } catch (IOException e) {
                    throw new RuntimeException("Error while saving banner files", e);
                }
            });

        } else {
            // New entry: Save both banners
            File destinationMobileBanner = new File(destinationDir, mobileNewFileName);
            File destinationDesktopBanner = new File(destinationDir, desktopNewFileName);

            if (mobileBanner != null) {
                mobileBanner.transferTo(destinationMobileBanner);
            }
            if (desktopBanner != null) {
                desktopBanner.transferTo(destinationDesktopBanner);
            }

            ProjectBanner projectBanner = new ProjectBanner();
            projectBanner.setProjectId(projectBannerDto.getProjectId());
            projectBanner.setDesktopBanner(desktopNewFileName);
            projectBanner.setMobileBanner(mobileNewFileName);
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

    public Response deleteBanner(int id){
        Response response = new Response();
        try{
            Optional<ProjectBanner> byId = projectBannerRepository.findById(id);
            byId.ifPresent(p->{
                projectBannerRepository.deleteById(id);
            });
            return new Response(1, "Banner deleted successfully...");
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
