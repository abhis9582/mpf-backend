package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.entities.Amenity;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectAmenity;
import com.mypropertyfact.estate.models.ProjectAmenityDto;
import com.mypropertyfact.estate.models.ProjectAmenityResponse;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectAmenityRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectAmenityService {
    @Autowired
    private ProjectAmenityRepository projectAmenityRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<ProjectAmenityDto> getAllProjectAmenity(){
        return null;
    }

    public Response addProjectAmenity(ProjectAmenityDto projectAmenityDto){
        Response response = new Response();
        try{
            if(projectAmenityDto.getProjectId() == 0 || projectAmenityDto.getAmenityList() == null){
                response.setMessage(Constants.ALL_FIELDS_REQUIRED);
                return response;
            }

            Project project = this.projectRepository.findById(projectAmenityDto.getProjectId()).get();
            if(project != null){
                projectAmenityDto.setSlugURL(project.getSlugURL());
            }
            if(projectAmenityDto.getId() > 0){
                ProjectAmenity amenity = this.projectAmenityRepository.findById(projectAmenityDto.getId()).get();
                if(amenity != null){
                    for(int i=0;i < projectAmenityDto.getAmenityList().size();i++) {
                        amenity.setAmenityId(projectAmenityDto.getAmenityList().get(i).getId());
                        amenity.setProjectId(projectAmenityDto.getProjectId());
                        this.projectAmenityRepository.save(amenity);
                    }
                    response.setIsSuccess(1);
                    response.setMessage("Amenity updated successfully...");
                }
            }else{
                for(int i=0;i < projectAmenityDto.getAmenityList().size();i++) {
                    ProjectAmenity amenity = new ProjectAmenity();
                    amenity.setProjectId(projectAmenityDto.getProjectId());
                    amenity.setSlugURL(projectAmenityDto.getSlugURL());
                    amenity.setAmenityId(projectAmenityDto.getAmenityList().get(i).getId());
                    this.projectAmenityRepository.save(amenity);
                }
                response.setIsSuccess(1);
                response.setMessage("Amenity saved successfully...");
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Amenity> getBySlug(String url) {
        return this.projectAmenityRepository.findBySlugURL(url);
    }
}
