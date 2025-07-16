package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectTypes;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.projections.ProjectTypeView;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import com.mypropertyfact.estate.repositories.ProjectTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProjectTypesService {
    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<ProjectTypeView> getAllProjectTypes() {
        return this.projectTypeRepository.findAllProjectedBy(Sort.by(Sort.Direction.ASC, "projectTypeName"));
    }

    public Response addUpdateProjectType(ProjectTypes projectTypes) {
        Response response = new Response();
        try {
            if (projectTypes == null || projectTypes.getProjectTypeName().isEmpty()) {
                response.setMessage("Project Type is required !");
                return response;
            }
            ProjectTypes existingData = this.projectTypeRepository.findByProjectTypeName(projectTypes.getProjectTypeName());
            if (existingData != null && existingData.getId() != projectTypes.getId()) {
                response.setMessage("Project type already exists...");
                return response;
            }
            String slugUrl = projectTypes.getProjectTypeName().toLowerCase(); // Convert to lowercase
            String[] words = slugUrl.split(" "); // Split the string by spaces
            StringBuilder result = new StringBuilder();

            // Iterate over the words
            for (int i = 0; i < words.length; i++) {
                result.append(words[i]); // Add the current word to the result
                // If it's not the last word, add a hyphen
                if (i < words.length - 1) {
                    result.append("-");
                }
            }
            // The result is the slug URL
            String finalSlug = result.toString();
            projectTypes.setSlugUrl(finalSlug);


            if (projectTypes.getId() > 0) {
                ProjectTypes dbProjectTypes = this.projectTypeRepository.findById(projectTypes.getId()).get();
                if (dbProjectTypes != null) {
                    dbProjectTypes.setProjectTypeName(projectTypes.getProjectTypeName());
                    dbProjectTypes.setSlugUrl(projectTypes.getSlugUrl());
                    dbProjectTypes.setProjectTypeDesc(projectTypes.getProjectTypeDesc());
                    dbProjectTypes.setMetaDesc(projectTypes.getMetaDesc());
                    dbProjectTypes.setMetaKeyword(projectTypes.getMetaKeyword());
                    dbProjectTypes.setMetaTitle(projectTypes.getMetaTitle());
                    this.projectTypeRepository.save(dbProjectTypes);
                    response.setIsSuccess(1);
                    response.setMessage("Project type updated successfully...");
                }
            } else {
                this.projectTypeRepository.save(projectTypes);
                response.setMessage("Project type added successfully...");
                response.setIsSuccess(1);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Transactional
    public Map<String, Object> getBySlug(String url) {
        Optional<ProjectTypes> projectTypeData = this.projectTypeRepository.findBySlugUrl(url);
        Map<String, Object> responseObj = new HashMap<>();
        List<Project> projects = projectRepository.findAll();
        projectTypeData.ifPresent(projectType -> {
            responseObj.put("id", projectType.getId());
            responseObj.put("projectTypeName", projectType.getProjectTypeName());
            responseObj.put("projectTypeDesc", projectType.getProjectTypeDesc());
            responseObj.put("metaTitle", projectType.getMetaTitle());
            responseObj.put("metaKeyword", projectType.getMetaKeyword());
            responseObj.put("metaDesc", projectType.getMetaDesc());
            List<Map<String, Object>> projectList = new ArrayList<>();
            if(url.equals("new-launches")) {
                projectList = projects.stream().filter(project -> project.getProjectStatus() != null && project.getProjectStatus().getId() == 3).map(project-> {
                    Map<String, Object> projectObj = new HashMap<>();
                    projectObj.put("projectId", project.getId());
                    projectObj.put("projectName", project.getProjectName());
                    if(project.getCity() != null) {
                        projectObj.put("projectAddress", project.getProjectLocality().concat(", ").concat(project.getCity().getName()));
                    }
                    projectObj.put("projectThumbnail", project.getProjectThumbnail());
                    projectObj.put("projectPrice", project.getProjectPrice());
                    projectObj.put("slugURL", project.getSlugURL());
                    projectObj.put("projectStatus", project.getProjectStatus() != null ? project.getProjectStatus().getId(): "0");
                    projectObj.put("projectStatusName", project.getProjectStatus() != null ? project.getProjectStatus().getStatusName(): null);
                    if(project.getProjectTypes() != null) {
                        projectObj.put("typeName", project.getProjectTypes().getProjectTypeName());
                    }
                    return projectObj;
                }).toList();
            }else{
                projectList = projectType.getProject().stream().map(project-> {
                    Map<String, Object> projectObj = new HashMap<>();
                    projectObj.put("projectId", project.getId());
                    projectObj.put("projectName", project.getProjectName());
                    if(project.getCity() != null) {
                        projectObj.put("projectAddress", project.getProjectLocality().concat(", ").concat(project.getCity().getName()));
                    }
                    projectObj.put("projectThumbnail", project.getProjectThumbnail());
                    projectObj.put("projectPrice", project.getProjectPrice());
                    projectObj.put("slugURL", project.getSlugURL());
                    projectObj.put("projectStatus", project.getProjectStatus() != null ? project.getProjectStatus().getId(): "0");
                    projectObj.put("projectStatusName", project.getProjectStatus() != null ? project.getProjectStatus().getStatusName(): null);
                    if(project.getProjectTypes() != null) {
                        projectObj.put("typeName", project.getProjectTypes().getProjectTypeName());
                    }
                    return projectObj;
                }).toList();
            }
            responseObj.put("projects", projectList);
        });
        return responseObj;
    }

//    public List<Project> getPropertiesBySlug(String url) {
//        ProjectTypes projectTypes = this.projectTypeRepository.findBySlugUrl(url);
//        List<Project> projects = this.projectRepository.getAllProjectsByType(projectTypes.getId());
//        return projects;
//    }

    public Response deleteProjectType(int id) {
        this.projectTypeRepository.deleteById(id);
        return new Response(1, "Project type deleted successfully...");
    }

    public List<ProjectTypes> getAllProjectTypesList() {
        return projectTypeRepository.findAll();
    }
}
