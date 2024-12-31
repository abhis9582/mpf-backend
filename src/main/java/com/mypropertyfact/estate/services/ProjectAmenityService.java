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

    public List<ProjectAmenityResponse> getAllProjectAmenity() {
        List<Object[]> response = this.projectRepository.getAllProjectAmenity();
        return response.stream().map(item ->
                new ProjectAmenityResponse(
                        (int) item[0],
                        (String) item[1],
                        (String) item[2]
                )).collect(Collectors.toList());
    }
    public Response addProjectAmenity(ProjectAmenityDto projectAmenityDto) {
        Response response = new Response();
        try {
            if (projectAmenityDto.getProjectId() == 0 || projectAmenityDto.getAmenityList() == null) {
                response.setMessage(Constants.ALL_FIELDS_REQUIRED);
                return response;
            }

            Project project = this.projectRepository.findById(projectAmenityDto.getProjectId()).get();
            if (project != null) {
                projectAmenityDto.setSlugURL(project.getSlugURL());
            }
            List<ProjectAmenity> dbAmenity = this.projectAmenityRepository.findByProjectId(projectAmenityDto.getProjectId());
            if (dbAmenity.size() > 0) {
                this.projectAmenityRepository.deleteByProjectId(projectAmenityDto.getProjectId());
            }
            for (int i = 0; i < projectAmenityDto.getAmenityList().size(); i++) {
                ProjectAmenity amenity = new ProjectAmenity();
                amenity.setProjectId(projectAmenityDto.getProjectId());
                amenity.setSlugURL(projectAmenityDto.getSlugURL());
                amenity.setAmenityId(projectAmenityDto.getAmenityList().get(i).getId());
                this.projectAmenityRepository.save(amenity);
            }
            response.setIsSuccess(1);
            response.setMessage("Amenity saved successfully...");
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Amenity> getBySlug(String url) {
        return this.projectAmenityRepository.findBySlugURL(url);
    }

    public List<Amenity> getById(int id) {
        return this.projectAmenityRepository.findListByProjectId(id);
    }
    public Response deleteProjectAmenity(int projectId){
        this.projectAmenityRepository.deleteByProjectId(projectId);
        return new Response(1, "Project amenity deleted successfully...");
    }
}
