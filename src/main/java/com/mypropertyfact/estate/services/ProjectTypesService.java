package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.ProjectTypes;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTypesService {
    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    public List<ProjectTypes> getAllProjectTypes() {
        return this.projectTypeRepository.findAll();
    }

    public Response addUpdateProjectType(ProjectTypes projectTypes){
        Response response = new Response();
        try{
            if(projectTypes == null || projectTypes.getProjectTypeName().isEmpty()){
                response.setMessage("Project Type is required !");
                return response;
            }
            ProjectTypes existingData = this.projectTypeRepository.findByProjectTypeName(projectTypes.getProjectTypeName());
            if(existingData != null && existingData.getId() != projectTypes.getId()){
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


            if(projectTypes.getId() > 0){
                ProjectTypes dbProjectTypes = this.projectTypeRepository.findById(projectTypes.getId()).get();
                if(dbProjectTypes != null){
                    dbProjectTypes.setProjectTypeName(projectTypes.getProjectTypeName());
                    dbProjectTypes.setSlugUrl(projectTypes.getSlugUrl());
                    dbProjectTypes.setProjectTypeDesc(projectTypes.getProjectTypeDesc());
                    this.projectTypeRepository.save(dbProjectTypes);
                    response.setIsSuccess(1);
                    response.setMessage("Project type updated successfully...");
                }
            }else{
                this.projectTypeRepository.save(projectTypes);
                response.setMessage("Project type added successfully...");
                response.setIsSuccess(1);
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
    public ProjectTypes getBySlug(String url){
        return this.projectTypeRepository.findBySlugUrl(url);
    }
}
