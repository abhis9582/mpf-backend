package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.FaqResponse;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectFaqs;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectFaqsRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectFaqsService {
    @Autowired
    private ProjectFaqsRepository projectFaqsRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<FaqResponse> getAllFaqs(){
        List<Object[]> response = this.projectFaqsRepository.getAllWithProjectName();
        return response.stream().map(item->
                new FaqResponse(
                        (String)item[0],
                        (String)item[1],
                        (String)item[2],
                        (int)item[3],
                        (int)item[4]
                )).collect(Collectors.toList());
    }
    public Response addUpdateFaqs(ProjectFaqs projectFaqs){
        Response response = new Response();
        try{
            if(projectFaqs == null || projectFaqs.getFaqQuestion().isEmpty() || projectFaqs.getFaqAnswer().isEmpty()){
                response.setMessage("All fields are required !");
            }
            Project project = this.projectRepository.findById(projectFaqs.getProjectId()).get();
            projectFaqs.setSlugUrl(project.getSlugURL());
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

    public Response deleteFaq(int id){
        Response response = new Response();
        try{
            Optional<ProjectFaqs> byId = projectFaqsRepository.findById(id);
            if(byId.isPresent()){
                projectFaqsRepository.deleteById(id);
                response.setMessage("FAQ deleted successfully...");
                response.setIsSuccess(1);
            }else{
                response.setMessage("FAQ already deleted or not exists");
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
