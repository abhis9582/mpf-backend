package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.BuilderResponse;
import com.mypropertyfact.estate.entities.Builder;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.BuilderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BuilderService {
    @Autowired
    private BuilderRepository builderRepository;

    //Getting all builders
    public BuilderResponse getAllBuilders() {
        return new BuilderResponse(builderRepository.findAllProjectedBy(Sort.by(Sort.Direction.ASC, "builderName")),
                new Response(1, "All builders fetched successfully..."));
    }

    public Response addUpdateBuilder(Builder builder) {
        Response response = new Response();
        try {
            if (builder == null || builder.getBuilderName().isEmpty()) {
                return new Response(0, "Builder name is required !");
            }
            Builder existsBuilder = this.builderRepository.findByBuilderName(builder.getBuilderName());
            if (existsBuilder != null && existsBuilder.getId() != builder.getId()) {
                response.setMessage("Builder already exists!");
                response.setIsSuccess(0);
                return response;
            }
            String slugUrl = builder.getBuilderName(); // Convert to lowercase
            String[] words = slugUrl.toLowerCase().split(" "); // Split the string by spaces
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
            builder.setSlugUrl(finalSlug);
            if (builder.getId() != 0) {
                Builder savedBuilder = this.builderRepository.findById(builder.getId()).get();
                savedBuilder.setBuilderName(builder.getBuilderName());
                savedBuilder.setSlugUrl(builder.getSlugUrl());
                savedBuilder.setBuilderDesc(builder.getBuilderDesc());
                savedBuilder.setMetaTitle(builder.getMetaTitle());
                savedBuilder.setMetaKeyword(builder.getMetaKeyword());
                savedBuilder.setMetaDesc(builder.getMetaDesc());
                this.builderRepository.save(savedBuilder);
                response.setIsSuccess(1);
                response.setMessage("Builder Updated Successfully...");
            } else {
                this.builderRepository.save(builder);
                response.setMessage("Builder saved successfully...");
                response.setIsSuccess(1);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setIsSuccess(0);
        }
        return response;
    }

    public Response deleteBuilder(int id) {
        Response response = new Response();
        try {
            this.builderRepository.deleteById(id);
            response.setIsSuccess(1);
            response.setMessage("Builder Deleted Successfully...");
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setIsSuccess(0);
        }
        return response;
    }

    @Transactional
    public Map<String, Object> getBySlug(String url){
        Optional<Builder> dbBuilder = this.builderRepository.findBySlugUrl(url);
        Map<String, Object> builderObj = new HashMap<>();
        dbBuilder.ifPresent(builder-> {
            builderObj.put("id", builder.getId());
            builderObj.put("builderName", builder.getBuilderName());
            builderObj.put("builderDesc", builder.getBuilderDesc());
            builderObj.put("metaTitle", builder.getMetaTitle());
            builderObj.put("metaKeyword", builder.getMetaKeyword());
            builderObj.put("metaDesc", builder.getMetaDesc());
            List<Map<String, Object>> projectList = new ArrayList<>();
            projectList = builder.getProjects().stream().map(project-> {
                Map<String, Object> projectObj = new HashMap<>();
                projectObj.put("projectId", project.getId());
                projectObj.put("projectName", project.getProjectName());
                if(project.getCity() != null) {
                    projectObj.put("projectAddress", project.getProjectLocality() + project.getCity().getName());
                }
                projectObj.put("projectThumbnail", project.getProjectThumbnail());
                projectObj.put("projectPrice", project.getProjectPrice());
                projectObj.put("slugURL", project.getSlugURL());
                return projectObj;
            }).toList();
            builderObj.put("projects", projectList);
        });
        return builderObj;
    }

    public List<Builder> getAllBuildersList() {
        return builderRepository.findAll();
    }
}
