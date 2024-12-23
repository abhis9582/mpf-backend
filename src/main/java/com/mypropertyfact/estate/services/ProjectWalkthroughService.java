package com.mypropertyfact.estate.services;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectWalkthrough;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import com.mypropertyfact.estate.repositories.ProjectWalkthroughRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectWalkthroughService {
    @Autowired
    private ProjectWalkthroughRepository projectWalkthroughRepository;
    @Autowired
    private ProjectRepository projectRepository;
    public List<ProjectWalkthrough> getAllWalkthrough(){
        return this.projectWalkthroughRepository.findAll();
    }

    public Response addUpdate(ProjectWalkthrough projectWalkthrough){
        Response response = new Response();
        try{
            if(projectWalkthrough.getWalkthroughDesc().isEmpty()){
                response.setMessage("All fields are required !");
                return response;
            }
            Project project = this.projectRepository.findById(projectWalkthrough.getProjectId()).get();
            projectWalkthrough.setSlugURL(project.getSlugURL());
            if(projectWalkthrough.getId() > 0){
                ProjectWalkthrough savedWalkthrough = this.projectWalkthroughRepository.findById(projectWalkthrough.getId()).get();
                if(savedWalkthrough != null){
                    savedWalkthrough.setWalkthroughDesc(projectWalkthrough.getWalkthroughDesc());
                    savedWalkthrough.setUpdatedAt(LocalDateTime.now());
                    savedWalkthrough.setSlugURL(projectWalkthrough.getSlugURL());
                    this.projectWalkthroughRepository.save(savedWalkthrough);
                    response.setMessage("Walkthrough updated successfully...");
                    response.setIsSuccess(1);
                }
            }else {
                this.projectWalkthroughRepository.save(projectWalkthrough);
                response.setMessage("Walkthrough saved successfully...");
                response.setIsSuccess(1);
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteWalkthrough(int id){
        this.projectWalkthroughRepository.deleteById(id);
        return new Response(1, "Deleted");
    }
    public ProjectWalkthrough getBySlug(String url){
        return this.projectWalkthroughRepository.findBySlugURL(url);
    }
}
