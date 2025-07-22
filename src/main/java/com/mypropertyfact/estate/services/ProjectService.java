package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.*;
import com.mypropertyfact.estate.entities.*;
import com.mypropertyfact.estate.models.ProjectAmenityDto;
import com.mypropertyfact.estate.models.ProjectDto;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private BuilderRepository builderRepository;

    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private ProjectStatusRepository projectStatusRepository;

    @Value("${upload_dir}")
    private String uploadDir;

    public Page<ProjectDto> getAllProjects(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.ASC, "projectName"));
        Page<Project> projects = projectRepository.findAll(pageable);
        return projects.map(project-> {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setProjectPrice(project.getProjectPrice());
            projectDto.setProjectBy(project.getBuilder() != null ? project.getBuilder().getBuilderName(): null);
            projectDto.setProjectName(project.getProjectName());
            projectDto.setSlugURL(project.getSlugURL());
            projectDto.setProjectPrice(project.getProjectPrice());
            projectDto.setProjectConfiguration(project.getProjectConfiguration());
            projectDto.setProjectLocality(project.getProjectLocality());
            projectDto.setCity(project.getCity() != null ? project.getCity().getName() : null);
            projectDto.setPropertyType(project.getProjectTypes() != null ? project.getProjectTypes().getProjectTypeName(): null);
            projectDto.setProjectThumbnailImage(project.getProjectThumbnail());
            projectDto.setProjectAddress(project.getProjectLocality().concat(", ").concat(projectDto.getCity()));
            projectDto.setTypeName(project.getProjectTypes() != null ? project.getProjectTypes().getProjectTypeName(): null);
            projectDto.setProjectStatusName(project.getProjectStatus() != null ? project.getProjectStatus().getStatusName(): null);
            return projectDto;
        });
    }
//    public List<ProjectView> getAllProjects() {
//        projectRepository.findAll();
//        return projectRepository.findAllProjectedBy(Sort.by(Sort.Direction.ASC, "projectName"));
//    }

    @Transactional
    public Map<String, Object> getBySlugUrl(String url) {
        Optional<Project> projectData = projectRepository.findBySlugURL(url);
        Map<String, Object> projectObj = new HashMap<>();
        projectData.ifPresent(project -> {
            projectObj.put("id", project.getId());
            projectObj.put("projectName", project.getProjectName());
            projectObj.put("metaTitle", project.getMetaTitle());
            projectObj.put("metaDescription", project.getMetaDescription());
            projectObj.put("metaKeyWord", project.getMetaKeyword());
            projectObj.put("projectPrice", project.getProjectPrice());
            projectObj.put("reraNo", project.getReraNo());
            projectObj.put("amenityDesc", project.getAmenityDesc());
            projectObj.put("floorPlanDesc", project.getFloorPlanDesc());
            projectObj.put("locationDesc", project.getLocationDesc());
            projectObj.put("locationMapImage", project.getLocationMap());
            projectObj.put("projectThumbnail", project.getProjectThumbnail());
            projectObj.put("projectLogo", project.getProjectLogo());
            projectObj.put("slugURL", project.getSlugURL());
            if (project.getCity() != null) {
                projectObj.put("cityId", project.getCity().getId());
                projectObj.put("cityName", project.getCity().getName());
                projectObj.put("projectAddress", project.getProjectLocality().concat(", ") + project.getCity().getName());
                if (project.getCity().getState() != null) {
                    projectObj.put("stateId", project.getCity().getState().getId());
                    projectObj.put("stateName", project.getCity().getState().getStateName());
                    if (project.getCity().getState().getCountry() != null) {
                        projectObj.put("countryId", project.getCity().getState().getCountry().getId());
                        projectObj.put("countryName", project.getCity().getState().getCountry().getCountryName());
                    }
                }
            }
            if (project.getProjectsAbout() != null) {
                projectObj.put("aboutId", project.getProjectsAbout().getId());
                projectObj.put("projectShortDesc", project.getProjectsAbout().getShortDesc());
                projectObj.put("projectLongDesc", project.getProjectsAbout().getLongDesc());
            }
            if (project.getProjectWalkthrough() != null) {
                projectObj.put("walkthroughDesc", project.getProjectWalkthrough().getWalkthroughDesc());
            }
            List<FloorPlanDto> floorPlanList = new ArrayList<>();
            if (project.getFloorPlans() != null) {
                floorPlanList = project.getFloorPlans().stream().filter(Objects::nonNull)
                        .map(floorPlan -> {
                            FloorPlanDto floorPlanDto = new FloorPlanDto();
                            floorPlanDto.setFloorId(floorPlan.getId());
                            floorPlanDto.setPlanType(floorPlan.getPlanType());
                            floorPlanDto.setAreaSqMt(floorPlan.getAreaSqmt());
                            floorPlanDto.setAreaSqFt(floorPlan.getAreaSqft());
                            return floorPlanDto;
                        }).toList();
            }
            projectObj.put("floorPlan", floorPlanList);
            List<ProjectBannerDto> bannerList = new ArrayList<>();
            if (project.getProjectBanners() != null) {
                bannerList = project.getProjectBanners().stream().filter(Objects::nonNull)
                        .map(projectBanner -> {
                            ProjectBannerDto projectBannerDto = new ProjectBannerDto();
                            projectBannerDto.setDesktopImage(projectBanner.getDesktopBanner());
                            projectBannerDto.setMobileImage(projectBanner.getMobileBanner());
                            return projectBannerDto;
                        }).toList();
            }
            projectObj.put("banners", bannerList);
            List<AmenityDto> amenityList = new ArrayList<>();
            if (project.getAmenities() != null) {
                amenityList = project.getAmenities().stream().filter(Objects::nonNull)
                        .map(amenity -> {
                            AmenityDto amenityDto = new AmenityDto();
                            amenityDto.setId(amenity.getId());
                            amenityDto.setTitle(amenity.getTitle());
                            amenityDto.setImage(amenity.getAmenityImageUrl());
                            return amenityDto;
                        }).toList();
            }
            projectObj.put("amenities", amenityList);

            List<ProjectGalleryDto> projectGalleryList = new ArrayList<>();
            if (project.getProjectGalleries() != null) {
                projectGalleryList = project.getProjectGalleries().stream().filter(Objects::nonNull)
                        .map(projectGallery -> {
                            ProjectGalleryDto projectGalleryDto = new ProjectGalleryDto();
                            projectGalleryDto.setGalleyImage(projectGallery.getImage());
                            return projectGalleryDto;
                        }).toList();
            }
            projectObj.put("projectGalleryImages", projectGalleryList);
            List<LocationBenefitDto> locationBenefitList = new ArrayList<>();
            if (project.getLocationBenefits() != null) {
                locationBenefitList = project.getLocationBenefits().stream().filter(Objects::nonNull)
                        .map(locationBenefit -> {
                            LocationBenefitDto locationBenefitDto = new LocationBenefitDto();
                            locationBenefitDto.setImage(locationBenefit.getIconImage());
                            locationBenefitDto.setBenefitName(locationBenefit.getBenefitName());
                            locationBenefitDto.setDistance(locationBenefit.getDistance());
                            return locationBenefitDto;
                        }).toList();
            }
            projectObj.put("locationBenefits", locationBenefitList);

            List<ProjectFaqDto> projectFaqList = new ArrayList<>();
            if (project.getProjectFaqs() != null) {
                projectFaqList = project.getProjectFaqs().stream().filter(Objects::nonNull)
                        .map(projectFaq -> {
                            ProjectFaqDto projectFaqDto = new ProjectFaqDto();
                            projectFaqDto.setQuestion(projectFaq.getFaqQuestion());
                            projectFaqDto.setAnswer(projectFaq.getFaqAnswer());
                            projectFaqDto.setId(projectFaq.getId());
                            return projectFaqDto;
                        }).toList();
            }
            projectObj.put("projectFaqs", projectFaqList);
        });
        return projectObj;
    }

    @Transactional
    public Response deleteProject(int id) {
        Response response = new Response();
        try {
            if (id > 0) {
                Optional<Project> optionalProject = projectRepository.findById(id);
                if (optionalProject.isPresent()) {
                    Project project = optionalProject.get();

                    // 1. Clear project-amenities relationship
                    project.getAmenities().clear();
                    projectRepository.save(project); // Persist the relationship removal

                    // 2. Delete the directory (if applicable)
                    String dirPath = uploadDir.concat("properties/") + project.getSlugURL();
                    deleteDirectory(dirPath); // Make sure this handles exceptions safely

                    // 3. Delete the project
                    projectRepository.delete(project);

                    // 4. Set success response
                    response.setMessage("Project deleted successfully...");
                    response.setIsSuccess(1);
                } else {
                    response.setMessage("No project found with the given ID!");
                    response.setIsSuccess(0);
                }
            } else {
                response.setMessage("Invalid project ID!");
                response.setIsSuccess(0);
            }
        } catch (Exception e) {
            response.setMessage("Error deleting project: " + e.getMessage());
            response.setIsSuccess(0);
            e.printStackTrace();
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
            Optional<Project> dbProject = projectRepository.findById(projectDto.getId());

            // Generate slug URL if empty
            if (projectDto.getSlugURL().isEmpty() && projectDto.getProjectName() != null) {
                projectDto.setSlugURL(fileUtils.generateSlug(projectDto.getProjectName()));
            }

            // Generating path for storing image
            String projectDir = uploadDir.concat("properties/") + projectDto.getSlugURL();
            createDirectory(projectDir);
            // Process images only if new files are provided
            dbProject.ifPresent(project -> {
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
                response.setMessage(Constants.PROJECT_UPDATED);
                response.setIsSuccess(1);
            });
            if (dbProject.isEmpty()) {
                Project newProject = new Project();
                newProject.setProjectLogo(processFile(projectLogo, projectDir));
                newProject.setLocationMap(processFile(locationMap, projectDir));
                newProject.setProjectThumbnail(processFile(projectThumbnail, projectDir));
                mapDtoToEntity(newProject, projectDto);
                projectRepository.save(newProject);
                response.setMessage(Constants.PROJECT_SAVED);
                response.setIsSuccess(1);
            }
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
    private String processFile(MultipartFile file, String uploadDir) {
        if (file != null && file.getContentType().startsWith("image/")) {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "." + extension;
            Path filePath = Paths.get(uploadDir, fileName);
            try {
                Files.copy(file.getInputStream(), filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return fileName;
        }
        return "";
    }

    // Map DTO fields to Project entity
    private void mapDtoToEntity(Project project, ProjectDto dto) {
        Optional<City> cityObj = cityRepository.findById(Integer.parseInt(dto.getCity()));
        Optional<Builder> builderObj = builderRepository.findById(Integer.parseInt(dto.getProjectBy()));
        Optional<ProjectTypes> projectTypeObj = projectTypeRepository.findById(Integer.parseInt(dto.getPropertyType()));
        Optional<ProjectStatus> projectStatus = projectStatusRepository.findById(Integer.parseInt(dto.getProjectStatus()));
        project.setMetaTitle(dto.getMetaTitle());
        project.setMetaDescription(dto.getMetaDescription());
        project.setMetaKeyword(dto.getMetaKeyword());
        project.setProjectName(dto.getProjectName());
        cityObj.ifPresent(project::setCity);
        builderObj.ifPresent(project::setBuilder);
        projectTypeObj.ifPresent(project::setProjectTypes);
        project.setProjectLocality(dto.getProjectLocality());
        project.setProjectConfiguration(dto.getProjectConfiguration());
        project.setProjectPrice(dto.getProjectPrice());
        project.setIvrNo(dto.getIvrNo());
        project.setReraNo(dto.getReraNo());
        project.setReraWebsite(dto.getReraWebsite());
        projectStatus.ifPresent(project::setProjectStatus);
        project.setSlugURL(dto.getSlugURL());
        project.setShowFeaturedProperties(true);
        project.setAmenityDesc(dto.getAmenityDesc());
        project.setLocationDesc(dto.getLocationDesc());
        project.setFloorPlanDesc(dto.getFloorPlanDesc());
        project.setStatus(true);
    }
    @Transactional
    public List<Map<String, Object>> searchByPropertyTypeLocationBudget(String propertyType, String propertyLocation, String budget) {
        List<Project> projects = projectRepository.findAll(Sort.by(Sort.Direction.ASC, "projectName"));
        List<Project> filteredList = projects;
        int start = 0;
        int end = 0;
        switch (budget) {
            case "Up to 1Cr*" -> {
                end = 1;
            } case "1-3 Cr*" -> {
                start = 1;
                end = 3;
            }
            case "3-5 Cr*" -> {
                start = 3;
                end = 5;
            }
            case "Above 5 Cr*" -> {
                start = 5;
                end = 20;
            }
            default -> end = 20;
        }
        final int s = start;
        final int e = end;
        try{
        if (propertyType != null && !propertyType.isEmpty() && propertyLocation.isEmpty()) {
            filteredList = projects.stream()
                    .filter(project -> project.getProjectTypes().getId() == Integer.parseInt(propertyType))
                    .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                    .toList();
        }else if(propertyLocation != null && !propertyLocation.isEmpty()){
            filteredList = projects.stream()
                    .filter(project -> project.getCity().getId() == Integer.parseInt(propertyLocation))
                    .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                    .toList();
        }else {
            filteredList = projects.stream()
                    .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                    .toList();
        }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return filteredList.stream().map(project -> {
            Map<String, Object> projectObj = new HashMap<>();
            projectObj.put("id", project.getId());
            projectObj.put("slugURL", project.getSlugURL());
            projectObj.put("projectThumbnail", project.getProjectThumbnail());
            projectObj.put("projectName", project.getProjectName());
            projectObj.put("projectPrice", project.getProjectPrice());
            if (project.getCity() != null) {
                projectObj.put("projectAddress", project.getProjectLocality().concat(" , ").concat(project.getCity().getName()));
            }
            if (project.getProjectTypes() != null) {
                projectObj.put("typeName", project.getProjectTypes().getProjectTypeName());
            }
            if(project.getProjectStatus() != null) {
                projectObj.put("projectStatusName", project.getProjectStatus().getStatusName());
            }
            return projectObj;
        }).toList();
    }

    public List<Map<String, Object>> getAllProjectsList() {
        List<Project> projects = projectRepository.findAll(Sort.by(Sort.Direction.ASC, "projectName"));
        return projects.stream().map(project -> {
            Map<String, Object> projectResponse = new HashMap<>();
            Optional<City> cityObj = cityRepository.findById(project.getCity().getId());
//            ProjectDto projectDto = new ProjectDto();
//            projectDto.setId(project.getId());
//            projectDto.setProjectBy(project.getBuilder().getId());
//            projectDto.setProjectName(project.getProjectName());
//            projectDto.set();
//            cityObj.ifPresent(city-> {
//                projectDto.setCity(city.getName());
//                projectDto.setState(city.getState().getStateName());
//                projectDto.setCountry(city.getState().getCountry().getCountryName());
//            });
            projectResponse.put("id", project.getId());
            projectResponse.put("projectName", project.getProjectName());
            if (project.getBuilder() != null) {
                projectResponse.put("projectBy", project.getBuilder().getId());
                projectResponse.put("builderName", project.getBuilder().getBuilderName());
            }
            projectResponse.put("projectLocality", project.getProjectLocality());
            projectResponse.put("projectConfiguration", project.getProjectConfiguration());
            if (project.getProjectTypes() != null) {
                projectResponse.put("typeName", project.getProjectTypes().getProjectTypeName());
            }
            projectResponse.put("metaTitle", project.getMetaTitle());
            projectResponse.put("metaDescription", project.getMetaDescription());
            projectResponse.put("metaKeyword", project.getMetaKeyword());
            cityObj.ifPresent(city -> {
                if (city.getState() != null) {
                    if (city.getState().getCountry() != null) {
                        projectResponse.put("country", city.getState().getCountry().getId());
                        projectResponse.put("countryName", city.getState().getCountry().getCountryName());
                    }
                    projectResponse.put("state", city.getState().getId());
                    projectResponse.put("stateName", city.getState().getStateName());
                }
                projectResponse.put("city", city.getId());
                projectResponse.put("cityName", city.getName());
                projectResponse.put("projectAddress", project.getProjectLocality().concat(", ").concat(city.getName()));
            });
            projectResponse.put("country", project.getCity().getState().getCountry().getId());
            projectResponse.put("projectPrice", project.getProjectPrice());
            projectResponse.put("ivrNo", project.getIvrNo());
            projectResponse.put("locationMap", project.getLocationMap());
            projectResponse.put("reraNo", project.getReraNo());
            projectResponse.put("reraWebsite", project.getReraWebsite());
            projectResponse.put("propertyType", project.getProjectTypes().getId());
            projectResponse.put("projectThumbnail", project.getProjectThumbnail());
            projectResponse.put("slugURL", project.getSlugURL());
            projectResponse.put("amenityDesc", project.getAmenityDesc());
            projectResponse.put("locationDesc", project.getLocationDesc());
            projectResponse.put("floorPlanDesc", project.getFloorPlanDesc());
            projectResponse.put("projectLogo", project.getProjectLogo());
            projectResponse.put("projectStatus", project.getProjectStatus() != null ? project.getProjectStatus().getId(): "0");
            projectResponse.put("projectStatusName", project.getProjectStatus() != null ? project.getProjectStatus().getStatusName(): null);
            List<Map<String, Object>> amenityList = new ArrayList<>();
            amenityList = project.getAmenities().stream().filter(Objects::nonNull).map(amenity -> {
                Map<String, Object> amenityObj = new HashMap<>();
                amenityObj.put("id", amenity.getId());
                amenityObj.put("title", amenity.getTitle());
                return amenityObj;
            }).toList();
            projectResponse.put("amenities", amenityList);
            return projectResponse;
        }).toList();
    }

    @Transactional
    public Response addUpdateAmenity(ProjectAmenityDto dto) {
        Optional<Project> dbProject = projectRepository.findById(dto.getProjectId());

        if (dbProject.isEmpty()) {
            return new Response(0, "Project not found");
        }

        Project project = dbProject.get();

        List<Amenity> dtoAmenities = dto.getAmenityList();
        if (dtoAmenities == null || dtoAmenities.isEmpty()) {
            return new Response(0, "No amenities provided");
        }
        project.setAmenities(dtoAmenities);
        // Save the project and its associations
        projectRepository.save(project);

        return new Response(1, "Amenities saved successfully");
    }
    @Transactional
    public List<Project> getAllProjectWithAssociatedMappings() {
        return projectRepository.findAll();
    }
}
