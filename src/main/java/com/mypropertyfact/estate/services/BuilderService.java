package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.BuilderResponse;
import com.mypropertyfact.estate.entities.Builder;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.BuilderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuilderService {
    @Autowired
    private BuilderRepository builderRepository;

    public BuilderResponse getAllBuilders() {
        BuilderResponse builderResponse = new BuilderResponse();
        try {
            builderResponse.setBuilders(this.builderRepository.findAll());
            builderResponse.setResponse(new Response(1, "All Builder fetched successfully..."));
        } catch (Exception e) {
            builderResponse.setResponse(new Response(0, e.getMessage()));
        }
        return builderResponse;
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
    public Builder getBySlug(String url){
        return this.builderRepository.findBySlugUrl(url);
    }
}
