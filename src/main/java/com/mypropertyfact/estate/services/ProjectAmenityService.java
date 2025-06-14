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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectAmenityService {
    @Autowired
    private ProjectAmenityRepository projectAmenityRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getAllProjectAmenity() {

        return this.projectRepository.findAll();
    }
    public Response addProjectAmenity(ProjectAmenityDto projectAmenityDto) {
        Response response = new Response();
        try {
            if (projectAmenityDto.getProjectId() == 0 || projectAmenityDto.getAmenityList() == null) {
                response.setMessage(Constants.ALL_FIELDS_REQUIRED);
                return response;
            }
            Optional<Project> project = this.projectRepository.findById(projectAmenityDto.getProjectId());
            project.ifPresent(value -> projectAmenityDto.setSlugURL(value.getSlugURL()));
            List<ProjectAmenity> dbAmenity = this.projectAmenityRepository.findByProjectId(projectAmenityDto.getProjectId());
            if (!dbAmenity.isEmpty()) {
                this.projectAmenityRepository.deleteByProjectId(projectAmenityDto.getProjectId());
            }
            for (int i = 0; i < projectAmenityDto.getAmenityList().size(); i++) {
                ProjectAmenity amenity = new ProjectAmenity();
                amenity.setSlugURL(projectAmenityDto.getSlugURL());
//                amenity.setAmenityId(projectAmenityDto.getAmenityList().get(i).getId());
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
