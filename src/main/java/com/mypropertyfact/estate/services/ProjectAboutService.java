package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectsAbout;
import com.mypropertyfact.estate.models.ProjectAboutResponse;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectAboutRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectAboutService {

    @Autowired
    private ProjectAboutRepository projectAboutRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<ProjectAboutResponse> getAllProjectsAbout(){
        List<Object[]> response = this.projectAboutRepository.getAllProjectAbout();
        return response.stream().map(item->
                new ProjectAboutResponse(
                        (int)item[0],
                        (int)item[1],
                        (String)item[2],
                        (String)item[3],
                        (String)item[4]
                )).collect(Collectors.toList());
    }

    public Response addUpdate(ProjectsAbout projectsAbout){
        Response response = new Response();
        try{
            if(projectsAbout.getLongDesc().isEmpty() || projectsAbout.getShortDesc().isEmpty()){
                response.setMessage(Constants.ALL_FIELDS_REQUIRED);
                return response;
            }
            Project project = this.projectRepository.findById(projectsAbout.getProjectId()).get();
            projectsAbout.setSlugURL(project.getSlugURL());
            if(projectsAbout.getId() > 0){
                ProjectsAbout savedProjectsAbout = this.projectAboutRepository.findById(projectsAbout.getId()).get();
                if(savedProjectsAbout != null){
                    savedProjectsAbout.setShortDesc(projectsAbout.getShortDesc());
                    savedProjectsAbout.setLongDesc(projectsAbout.getLongDesc());
                    savedProjectsAbout.setSlugURL(projectsAbout.getSlugURL());
                    savedProjectsAbout.setUpdatedAt(LocalDateTime.now());
                    this.projectAboutRepository.save(savedProjectsAbout);
                    response.setMessage("Project's about description updated successfully...");
                    response.setIsSuccess(1);
                }
            }else{
                this.projectAboutRepository.save(projectsAbout);
                response.setMessage("Project's about description saved successfully...");
                response.setIsSuccess(1);
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteProjectsAbout(int id){
        this.projectAboutRepository.deleteById(id);
        return new Response(1,"Data deleted successfully...");
    }
    public ProjectsAbout getBySlug(String url){
       return this.projectAboutRepository.findBySlugURL(url);
    }
}
