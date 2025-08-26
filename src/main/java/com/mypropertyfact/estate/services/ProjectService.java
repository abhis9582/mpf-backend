package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.common.CommonMapper;
import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.*;
import com.mypropertyfact.estate.dtos.AddUpdateProjectDto;
import com.mypropertyfact.estate.dtos.ProjectDetailDto;
import com.mypropertyfact.estate.entities.*;
import com.mypropertyfact.estate.models.ProjectAmenityDto;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private CommonMapper commonMapper;

    @Value("${upload_dir}")
    private String uploadDir; //D:/my-property-fact/public/

    @Transactional
    public List<ProjectDetailDto> fetchAllProjects() {
        List<Project> projects = projectRepository.findAll(Sort.by(Sort.Direction.ASC, "projectName"));
        return projects.stream().map(project -> {
            ProjectDetailDto detailDto = new ProjectDetailDto();
            commonMapper.mapFullProjectDetailToDetailedDto(project, detailDto);
            return detailDto;
        }).toList();
    }

    @Transactional
    public List<ProjectDetailDto> searchByPropertyTypeLocationBudget(String propertyType, String propertyLocation, String budget) {
//        List<Project> projects = projectRepository.findAllWithAllRelations();
        List<Project> projects = projectRepository.findAll(Sort.by(Sort.Direction.ASC, "projectName")).stream().filter(Project::isStatus).toList();
        List<Project> filteredList = projects;
        int start = 0;
        int end = 0;
        switch (budget) {
            case "Up to 1Cr*" -> {
                end = 1;
            }
            case "1-3 Cr*" -> {
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
        try {
            if (!propertyType.isEmpty() && propertyLocation.isEmpty()) {
                if (propertyType.equals("3")) {
                    filteredList = projects.stream()
                            .filter(project -> {
                                // Check for null and non-numeric price
                                String priceStr = project.getProjectPrice();
                                return isNumeric(priceStr);
                            })
                            .filter(project -> project.getProjectStatus().getStatusName().equals("New Launch"))
                            .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                            .toList();
                } else {
                    filteredList = projects.stream()
                            .filter(project -> {
                                // Check for null and non-numeric price
                                String priceStr = project.getProjectPrice();
                                return isNumeric(priceStr);
                            })
                            .filter(project -> project.getProjectTypes().getId() == Integer.parseInt(propertyType))
                            .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                            .toList();
                }
            } else if (!propertyLocation.isEmpty() &&
                    propertyType.trim().isEmpty()) {
                filteredList = projects.stream()
                        .filter(project -> {
                            // Check for null and non-numeric price
                            String priceStr = project.getProjectPrice();
                            return isNumeric(priceStr);
                        })
                        .filter(project -> project.getCity().getId() == Integer.parseInt(propertyLocation))
                        .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                        .toList();
            } else if (!propertyLocation.trim().isEmpty() &&
                    !propertyType.trim().isEmpty() && budget.isEmpty()) {
                if (propertyType.equals("3")) {
                    filteredList = projects.stream()
                            .filter(project -> {
                                // Check for null and non-numeric price
                                String priceStr = project.getProjectPrice();
                                return isNumeric(priceStr);
                            })
                            .filter(project -> project.getCity().getId() == Integer.parseInt(propertyLocation))
                            .filter(project -> project.getProjectStatus().getStatusName().equals("New Launch"))
                            .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                            .toList();
                } else {
                    filteredList = projects.stream()
                            .filter(project -> {
                                // Check for null and non-numeric price
                                String priceStr = project.getProjectPrice();
                                return isNumeric(priceStr);
                            })
                            .filter(project -> project.getCity().getId() == Integer.parseInt(propertyLocation))
                            .filter(project -> project.getProjectTypes().getId() == Integer.parseInt(propertyType))
                            .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                            .toList();
                }
            } else if (!propertyLocation.trim().isEmpty() &&
                    !propertyType.trim().isEmpty() && !budget.isEmpty()) {
                if (propertyType.equals("3")) {
                    filteredList = projects.stream()
                            .filter(project -> {
                                // Check for null and non-numeric price
                                String priceStr = project.getProjectPrice();
                                return isNumeric(priceStr);
                            })
                            .filter(project -> project.getCity().getId() == Integer.parseInt(propertyLocation))
                            .filter(project -> project.getProjectStatus().getStatusName().equals("New Launch"))
                            .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                            .toList();
                } else {
                    filteredList = projects.stream()
                            .filter(project -> {
                                // Check for null and non-numeric price
                                String priceStr = project.getProjectPrice();
                                return isNumeric(priceStr);
                            })
                            .filter(project -> project.getCity().getId() == Integer.parseInt(propertyLocation))
                            .filter(project -> project.getProjectTypes().getId() == Integer.parseInt(propertyType))
                            .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                            .toList();
                }
            } else {
                filteredList = projects.stream()
                        .filter(project -> {
                            // Check for null and non-numeric price
                            String priceStr = project.getProjectPrice();
                            return isNumeric(priceStr);
                        })
                        .filter(project -> Float.parseFloat(project.getProjectPrice()) > s && Float.parseFloat(project.getProjectPrice()) < e)
                        .toList();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return filteredList.stream().map(project -> {
            ProjectDetailDto detailDto = new ProjectDetailDto();
            commonMapper.mapProjectToProjectDto(project, detailDto);
            return detailDto;
        }).toList();
    }

    private boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public List<ProjectDetailDto> getAllProjectsList() {
        List<Project> projects = projectRepository.findAll(Sort.by(Sort.Direction.ASC, "projectName"));
        System.out.println("Total projects are " + projects.size());
        return projects.stream().map(project -> {
            ProjectDetailDto detailDto = new ProjectDetailDto();
            commonMapper.mapFullProjectDetailToDetailedDto(project, detailDto);
            return detailDto;
        }).toList();
    }

    @Transactional
    public Response addUpdateAmenity(ProjectAmenityDto dto) {
        Optional<Project> dbProject = projectRepository.findById(dto.getProjectId());

        if (dbProject.isEmpty()) {
            return new Response(0, "Project not found", 0);
        }

        Project project = dbProject.get();
        Set<Amenity> dtoAmenities = new HashSet<>();
        if (dto.getAmenityList() != null) {
            Set<Amenity> list = dto.getAmenityList().stream().map(amenity -> amenityRepository.findById(amenity.getId()).orElse(null)).collect(Collectors.toSet());
            dtoAmenities.addAll(list);
        }
        if (dtoAmenities.isEmpty()) {
            return new Response(0, "No amenities provided", 0);
        }
        project.setAmenities(dtoAmenities);
        // Save the project and its associations
        projectRepository.save(project);

        return new Response(1, "Amenities saved successfully", 0);
    }

    @Transactional
    public ProjectDetailDto getBySlugUrl(String url) {
        Optional<Project> projectData = projectRepository.findBySlugURLWithAllRelations(url);
        ProjectDetailDto detailDto = new ProjectDetailDto();
        projectData.ifPresent(project -> {
            commonMapper.mapFullProjectDetailToDetailedDto(project, detailDto);
        });
        return detailDto;
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

    @Transactional(rollbackFor = Exception.class)
    public Response saveProject(MultipartFile projectLogo,
                                MultipartFile locationMap,
                                MultipartFile projectThumbnail,
                                AddUpdateProjectDto addUpdateProjectDto) {
        Response response = new Response();
        List<String> savedFiles = new ArrayList<>();
        String fileDestination = null;
        try {
            Optional<Project> dbProject = Optional.empty();
            if (addUpdateProjectDto.getId() > 0) {
                dbProject = projectRepository.findById(addUpdateProjectDto.getId());
            }
            // Generate slug URL if empty
            if ((addUpdateProjectDto.getSlugURL() == null || addUpdateProjectDto.getSlugURL().isBlank())
                    && addUpdateProjectDto.getProjectName() != null
                    && !addUpdateProjectDto.getProjectName().isBlank()) {

                // If slug is missing/blank → generate from project name
                addUpdateProjectDto.setSlugURL(fileUtils.generateSlug(addUpdateProjectDto.getProjectName()));
            } else if (addUpdateProjectDto.getSlugURL() != null) {

                // If slug is provided → clean/normalize it
                addUpdateProjectDto.setSlugURL(fileUtils.generateSlug(addUpdateProjectDto.getSlugURL()));
            }
            Optional<Project> bySlugURL = Optional.empty();
            if (addUpdateProjectDto.getSlugURL() != null) {
                bySlugURL = projectRepository.findBySlugURL(addUpdateProjectDto.getSlugURL());
            }
            // Generating path for storing image
            String projectDir = null;
            if (!addUpdateProjectDto.getSlugURL().isBlank()) {
                projectDir = uploadDir.concat("properties/") + addUpdateProjectDto.getSlugURL();
                fileDestination = projectDir;
            }
            // Process images only if new files are provided
            if (dbProject.isPresent()) {
                Project project = dbProject.get();
                if (bySlugURL.isPresent() && bySlugURL.get().getId() != (addUpdateProjectDto.getId())) {
                    throw new IllegalArgumentException("SlugURL already exists!");
                }
                if (projectLogo != null && projectDir != null && !projectDir.isBlank()) {
                    fileUtils.deleteFileFromDestination(project.getProjectLogo(), projectDir);
                    if (!fileUtils.isValidAspectRatio(projectLogo.getInputStream(), 792, 203)) {
                        throw new IllegalArgumentException("Project logo should be of aspect ratio 3:9 or having dimension 792x203 or 390×100");
                    }
                    String savedProjectLogoImageName = processFile(projectLogo, projectDir, 792, 203);
                    project.setProjectLogo(savedProjectLogoImageName); //792 × 203 px Intrinsic aspect ratio:	792∶203
                    savedFiles.add(savedProjectLogoImageName);
                    project.setProjectThumbnailAltTag(fileUtils.generateImageAltTag(projectLogo));
                }
                if (locationMap != null && projectDir != null && !projectDir.isBlank()) {
                    fileUtils.deleteFileFromDestination(project.getLocationMap(), projectDir); //300∶193  900 × 579
                    if (!fileUtils.isValidAspectRatio(locationMap.getInputStream(), 815, 813)) {
                        throw new IllegalArgumentException("Location map image should be of aspect ratio 3:9 or having dimension 900x579 or 600×386");
                    }
                    String projectLocationMapImage = processFile(locationMap, projectDir, 815, 813);
                    project.setLocationMap(projectLocationMapImage);
                    savedFiles.add(projectLocationMapImage);
                    project.setLocationMapAltTag(fileUtils.generateImageAltTag(locationMap));
                }
                if (projectThumbnail != null && projectDir != null && !projectDir.isBlank()) {
                    fileUtils.deleteFileFromDestination(project.getProjectThumbnail(), projectDir);
                    if (!fileUtils.isValidAspectRatio(projectThumbnail.getInputStream(), 600, 600)) {
                        throw new IllegalArgumentException("Location map image should be of aspect ratio 1:1 or having dimension 600x600 or 400×400");
                    }
                    String projectThumbnailImage = processFile(projectThumbnail, projectDir, 600, 600);
                    project.setProjectThumbnail(projectThumbnailImage); //600 x 600 1:1
                    savedFiles.add(projectThumbnailImage);
                    project.setProjectThumbnailAltTag(fileUtils.generateImageAltTag(projectThumbnail));
                }
                //save data to database
                mapDtoToEntity(project, addUpdateProjectDto);
                projectRepository.save(project);
                response.setMessage(Constants.PROJECT_UPDATED);
                response.setIsSuccess(1);
            } else {
                Project newProject = new Project();
                if (bySlugURL.isPresent()) {
                    throw new IllegalArgumentException("SlugURL already exists !");
                }
                if (projectLogo != null) {
                    if (!fileUtils.isValidAspectRatio(projectLogo.getInputStream(), 792, 203)) {
                        throw new IllegalArgumentException("Project logo should be of aspect ratio 3:9 or having dimension 792x203 or 390×100");
                    }
                    String savedProjectLogoImageName = processFile(projectLogo, projectDir, 792, 203);
                    savedFiles.add(savedProjectLogoImageName);
                    newProject.setProjectLogo(savedProjectLogoImageName);
                }
                if (locationMap != null) {
                    if (!fileUtils.isValidAspectRatio(locationMap.getInputStream(), 815, 813)) {
                        throw new IllegalArgumentException("Location map image should be of aspect ratio 3:9 or having dimension 900x579 or 600×386");
                    }
                    String projectLocationMapImage = processFile(locationMap, projectDir, 815, 813);
                    newProject.setLocationMap(projectLocationMapImage);
                    savedFiles.add(projectLocationMapImage);
                }
                if (projectThumbnail != null) {
                    if (!fileUtils.isValidAspectRatio(projectThumbnail.getInputStream(), 600, 600)) {
                        throw new IllegalArgumentException("Location map image should be of aspect ratio 1:1 or having dimension 600x600 or 400×400");
                    }
                    String projectThumbnailImage = processFile(projectThumbnail, projectDir, 600, 600);
                    newProject.setProjectThumbnail(projectThumbnailImage); //600 x 600 1:1
                    savedFiles.add(projectThumbnailImage);
                }
                mapDtoToEntity(newProject, addUpdateProjectDto);
                Project savedProject = projectRepository.save(newProject);
                response.setMessage(Constants.PROJECT_SAVED);
                response.setIsSuccess(1);
                response.setProjectId(savedProject.getId());
            }
        } catch (Exception e) {
            // File cleanup if error
            for (String fileName : savedFiles) {
                try {
                    fileUtils.deleteFileFromDestination(fileName, fileDestination); // Pass directory if required
                } catch (Exception ex) {
                    System.err.println("Failed to delete file during rollback: " + fileName);
                }
            }
            response.setMessage(e.getMessage());
            // Re-throw to ensure DB rollback
            throw new RuntimeException(e);
        }
        return response;
    }

    // Generating file name and saving it to upload directory
    private String processFile(MultipartFile file, String uploadDir, int width, int height) {
        if (!file.isEmpty()) {
            if (fileUtils.isTypeImage(file)) {
                return fileUtils.saveDesktopImageWithResize(file, uploadDir, width, height, 0.85f);
            }
        }
        return "";
    }

    // Map DTO fields to Project entity
    private void mapDtoToEntity(Project project, AddUpdateProjectDto dto) {
        Optional<City> cityObj = Optional.empty();
        if (dto.getCityId() > 0) {
            cityObj = cityRepository.findById(dto.getCityId());
        }
        Optional<Builder> builderObj = Optional.empty();
        if (dto.getBuilderId() > 0) {
            builderObj = builderRepository.findById(dto.getBuilderId());
        }
        Optional<ProjectTypes> projectTypeObj = Optional.empty();
        if (dto.getPropertyTypeId() > 0) {
            projectTypeObj = projectTypeRepository.findById(dto.getPropertyTypeId());
        }
        Optional<ProjectStatus> projectStatus = Optional.empty();
        if (dto.getProjectStatusId() > 0) {
            projectStatus = projectStatusRepository.findById(dto.getProjectStatusId());
        }
        project.setMetaTitle(dto.getMetaTitle());
        project.setMetaDescription(dto.getMetaDescription());
        project.setMetaKeyword(dto.getMetaKeyword());
        project.setProjectName(dto.getProjectName());
        cityObj.ifPresent(project::setCity);
        builderObj.ifPresent(project::setBuilder);
        projectTypeObj.ifPresent(project::setProjectTypes);
        project.setProjectLocality(dto.getProjectLocality());
        project.setProjectConfiguration(dto.getProjectConfiguration());
        project.setProjectPrice(String.valueOf(dto.getProjectPrice()));
        project.setIvrNo(dto.getIvrNo());
        project.setReraNo(dto.getReraNo());
        project.setReraWebsite(dto.getReraWebsite());
        projectStatus.ifPresent(project::setProjectStatus);
        project.setSlugURL(dto.getSlugURL());
        project.setShowFeaturedProperties(true);
        project.setAmenityDesc(dto.getAmenityDescription());
        project.setLocationDesc(dto.getLocationDescription());
        project.setFloorPlanDesc(dto.getFloorPlanDescription());
        project.setStatus(dto.isStatus());
    }
}
