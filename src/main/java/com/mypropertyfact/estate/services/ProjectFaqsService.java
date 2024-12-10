package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.FaqResponse;
import com.mypropertyfact.estate.entities.ProjectFaqs;
import com.mypropertyfact.estate.entities.Property;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectFaqsRepository;
import com.mypropertyfact.estate.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectFaqsService {
    @Autowired
    private ProjectFaqsRepository projectFaqsRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    public List<FaqResponse> getAllFaqs(){
        List<Object[]> response = this.projectFaqsRepository.getAllWithProjectName();
        return response.stream().map(item->
                new FaqResponse(
                        (String)item[0],
                        (String)item[1],
                        (String)item[2]
                )).collect(Collectors.toList());
    }
    public Response addUpdateFaqs(ProjectFaqs projectFaqs){
        Response response = new Response();
        try{
            if(projectFaqs == null || projectFaqs.getFaqQuestion().isEmpty() || projectFaqs.getFaqAnswer().isEmpty()){
                response.setMessage("All fields are required !");
            }
            Property property = this.propertyRepository.findById(projectFaqs.getProjectId()).get();
            projectFaqs.setSlugUrl(property.getSlugURL());
            if(projectFaqs.getId() > 0){
                ProjectFaqs savedFaqs = this.projectFaqsRepository.findById(projectFaqs.getId()).get();
                if(savedFaqs != null){
                    savedFaqs.setFaqQuestion(projectFaqs.getFaqQuestion());
                    savedFaqs.setFaqAnswer(projectFaqs.getFaqAnswer());
                    savedFaqs.setSlugUrl(projectFaqs.getSlugUrl());
                    savedFaqs.setUpdatedAt(LocalDateTime.now());
                    this.projectFaqsRepository.save(savedFaqs);
                    response.setMessage("Faqs updated successfully...");
                    response.setIsSuccess(1);
                }
            }else{
                this.projectFaqsRepository.save(projectFaqs);
                response.setIsSuccess(1);
                response.setMessage("Faqs added successfully...");
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<ProjectFaqs> getBySlug(String url) {
        List<ProjectFaqs> response;
        try{
            response = this.projectFaqsRepository.findBySlugUrl(url);
        }catch (Exception e){
            response = new ArrayList<>();
        }
        return response;
    }
}
