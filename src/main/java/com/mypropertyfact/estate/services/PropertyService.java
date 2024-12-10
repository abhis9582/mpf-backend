package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.configs.dtos.ProjectWithBannerDTO;
import com.mypropertyfact.estate.configs.dtos.PropertyDetailDto;
import com.mypropertyfact.estate.entities.Property;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.PropertyRepository;
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
public class PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;
    @Value("${uploads_path}")
    private String uploadDir;
    public List<Property> getAllProperties() {
        return this.propertyRepository.findAll();
    }
    public List<ProjectWithBannerDTO> getAllProjectsWithMobileBanners() {
        List<Object[]> results = propertyRepository.getAllProjectsWithDesktopBanners();
        // Map the results to ProjectWithBannerDTO
        return results.stream()
                .map(result -> new ProjectWithBannerDTO(
                        (int) result[0],
                        (String) result[1], // projectName
                        (String) result[2],  // price
                        (String) result[3],  // location
                        (String) result[4],  // image (bannerUrl)
                        (String) result[5]   // (slug)
                ))
                .collect(Collectors.toList());
    }

    public Response postProperty(PropertyDetailDto propertyDetailDto) {
        Response response = new Response();
        try {
            // Validate property data
            if (propertyDetailDto.getProjectName().isEmpty() || propertyDetailDto.getMetaTitle().isEmpty() ||
                    propertyDetailDto.getMetaDescription().isEmpty() || propertyDetailDto.getMetaKeyword().isEmpty()) {
                response.setMessage("Title and project name are required.");
            }
            // Validate file type
            MultipartFile locationMap = propertyDetailDto.getLocationMap();
            MultipartFile projectLogo = propertyDetailDto.getProjectLogo();
            MultipartFile projectThumbnail = propertyDetailDto.getProjectThumbnail();
            if (locationMap.isEmpty() || projectLogo.isEmpty() || projectThumbnail.isEmpty()) {
                response.setMessage(Constants.NO_FILE_SELECTED);
            }

            String contentType = locationMap.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.setMessage(Constants.IMAGE_ALLOWED);
            }
            // Create directory if it doesn't exist
            File dir = new File(uploadDir + propertyDetailDto.getProjectName().trim());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generate location image name (UUID)
            String fileExtension = StringUtils.getFilenameExtension(locationMap.getOriginalFilename());
            String locationImageName = UUID.randomUUID().toString() + propertyDetailDto.getProjectName() + "." + fileExtension;

            // Generate logo image name (UUID)
            String logoExtension = StringUtils.getFilenameExtension(locationMap.getOriginalFilename());
            String logoImageName = UUID.randomUUID().toString() + propertyDetailDto.getProjectName() + "." + logoExtension;

            // Generate thumbnail image name (UUID)
            String thumbnailExtension = StringUtils.getFilenameExtension(locationMap.getOriginalFilename());
            String thumbnailImageName = UUID.randomUUID().toString() + propertyDetailDto.getProjectName() + "." + thumbnailExtension;

            // Save the file to the server
            Path path = Paths.get(dir.getPath() + "/" + locationImageName);
            Path logoPath = Paths.get(dir.getPath() + "/" + logoImageName);
            Path thumbNaliPath = Paths.get(dir.getPath() + "/" + thumbnailImageName);
//            Path path = Paths.get(dir.getPath() + "/" + locationImageName);
            Files.write(path, locationMap.getBytes());
            Files.write(logoPath, projectLogo.getBytes());
            Files.write(thumbNaliPath, projectThumbnail.getBytes());
//            Files.write(path, locationMap.getBytes());

            // Create and save user record in the database
            Property property = new Property();
            property.setMetaTitle(propertyDetailDto.getMetaTitle());
            property.setMetaKeyword(propertyDetailDto.getMetaKeyword());
            property.setMetaDescription(propertyDetailDto.getMetaDescription());
            property.setPropertyType(propertyDetailDto.getPropertyType());
            property.setProjectName(propertyDetailDto.getProjectName());
            property.setProjectAddress(property.getProjectAddress());
            property.setState(propertyDetailDto.getState());
            property.setCityLocation(propertyDetailDto.getCityLocation());
            property.setProjectLocality(propertyDetailDto.getProjectLocality());
            property.setProjectConfiguration(propertyDetailDto.getProjectConfiguration());
            property.setProjectBy(propertyDetailDto.getProjectBy());
            property.setProjectPrice(propertyDetailDto.getProjectPrice());
            property.setReraNo(propertyDetailDto.getReraNo());
            property.setProjectStatus(propertyDetailDto.getProjectStatus());
            property.setSlugURL(propertyDetailDto.getSlugURL());
            property.setStatus(true);
            property.setLocationMap(locationImageName);
            property.setProjectLogo(logoImageName);
            property.setProjectThumbnail(thumbnailImageName);
            property.setAboutDesc(propertyDetailDto.getAboutDesc());
            property.setAmenityDesc(propertyDetailDto.getAmenityDesc());
            property.setLocationDesc(propertyDetailDto.getLocationDesc());
            property.setWalkthroughDesc(propertyDetailDto.getWalkthroughDesc());
            property.setFloorPlanDesc(propertyDetailDto.getFloorPlanDesc());
            property.setProjectThumbnail(thumbnailImageName);
            property.setProjectBy(propertyDetailDto.getProjectBy());
            property.setProjectConfiguration(propertyDetailDto.getProjectConfiguration());
            property.setCountry(propertyDetailDto.getCountry());
            String [] slugUrl = propertyDetailDto.getProjectName().split(" ");
            String resultUrl = slugUrl[0];
            for(int i=1;i<slugUrl.length;i++){
                resultUrl+= "-"+slugUrl[i];
            }
            property.setSlugURL(resultUrl.toLowerCase());
            this.propertyRepository.save(property);
            response.setMessage(Constants.PROJECT_SAVED);
            response.setIsSuccess(1);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Property getBySlugUrl(String slugUrl) {
        try {
            return this.propertyRepository.findBySlugURL(slugUrl);
        }catch (Exception e){
            return new Property();
        }
    }
}
