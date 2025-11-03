package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.configs.dtos.ProjectAboutDto;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectsAbout;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectAboutRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjectAboutService {

    @Autowired
    private ProjectAboutRepository projectAboutRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<Map<String, Object>> getAllProjectsAbout(){
        List<ProjectsAbout> allProjectAbout = projectAboutRepository.findAll();
        return allProjectAbout.stream().map(about->{
            Map<String, Object> response= new HashMap<>();
            response.put("id", about.getId());
            response.put("longDesc", about.getLongDesc());
            response.put("shortDesc", about.getShortDesc());
            if(about.getProject() != null) {
                response.put("projectId", about.getProject().getId());
                response.put("projectName", about.getProject().getProjectName());
            }
            return response;
        }).toList();
    }

    public Response addUpdate(ProjectAboutDto projectAboutDto){
        Response response = new Response();
        try{
            if(projectAboutDto.getLongDesc().isEmpty() || projectAboutDto.getShortDesc().isEmpty()){
                response.setMessage(Constants.ALL_FIELDS_REQUIRED);
                return response;
            }
            Optional<Project> projectById = projectRepository.findById(projectAboutDto.getProjectId());
            if(projectAboutDto.getId() > 0){
                Optional<ProjectsAbout> saveData = projectAboutRepository.findById(projectAboutDto.getId());
                saveData.ifPresent(about-> {
                    about.setShortDesc(projectAboutDto.getShortDesc());
                    about.setLongDesc(projectAboutDto.getLongDesc());
                    projectById.ifPresent(about::setProject);
                    projectAboutRepository.save(about);
                    response.setMessage("Project's about details updated successfully...");
                    response.setIsSuccess(1);
                });
            }else{
                ProjectsAbout projectAbout = new ProjectsAbout();
                projectAbout.setLongDesc(projectAboutDto.getLongDesc());
                projectAbout.setShortDesc(projectAboutDto.getShortDesc());
                projectById.ifPresent(projectAbout::setProject);
                projectAboutRepository.save(projectAbout);
                response.setMessage("Project's about details saved successfully...");
                response.setIsSuccess(1);
            }
        } catch (DataIntegrityViolationException e) {
            // This exception occurs when a unique constraint is violated
            response.setMessage("This project already has 'about' details. Please update the existing entry.");
            response.setIsSuccess(0);
        } catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteProjectsAbout(int id){
        
        this.projectAboutRepository.deleteById(id);
        return new Response(1,"Data deleted successfully...", 0);
    }
//    public ProjectsAbout getBySlug(String url){
//        Project projectBySlugURL = projectRepository.findBySlugURL(url);
//        if(projectBySlugURL != null){
//
//        }
//        return projectAboutRepository.findBySlugURL(url);
//    }
}
