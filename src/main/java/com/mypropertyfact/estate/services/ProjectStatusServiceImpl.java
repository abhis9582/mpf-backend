package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.ProjectStatusDto;
import com.mypropertyfact.estate.entities.ProjectStatus;
import com.mypropertyfact.estate.interfaces.ProjectStatusService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectStatusServiceImpl implements ProjectStatusService {

    @Autowired
    private ProjectStatusRepository projectStatusRepository;

    @Override
    public Response addUpdate(ProjectStatusDto projectStatusDto) {
        if(projectStatusDto.getId() > 0) {
            Optional<ProjectStatus> projectStatus = projectStatusRepository.findById(projectStatusDto.getId());
            projectStatus.ifPresent(status-> {
                status.setStatusName(projectStatusDto.getStatusName());
                status.setCode(projectStatusDto.getStatusName().toUpperCase().replace(" ", "_"));
                status.setDescription(projectStatusDto.getDescription());
                status.setActive(!projectStatusDto.isActive() ? false: true);
                projectStatusRepository.save(status);
            });
            return new Response(1, "Project status updated successfully...");
        }else{
            ProjectStatus projectStatus = new ProjectStatus();
            projectStatus.setStatusName(projectStatusDto.getStatusName());
            projectStatus.setCode(projectStatusDto.getStatusName().toUpperCase().replace(" ", "_"));
            projectStatus.setDescription(projectStatusDto.getDescription());
            projectStatus.setActive(true);
            projectStatusRepository.save(projectStatus);
            return new Response(1, "Project status saved successfully...");
        }
    }

    @Override
    public Response deleteProjectStatus(int id) {
//        projectStatusRepository.deleteById(id);
        return null;
    }

    @Override
    public List<ProjectStatusDto> getAllStatus() {
        return projectStatusRepository.findAll().stream().map(status->{
            ProjectStatusDto projectStatusDto = new ProjectStatusDto();
            projectStatusDto.setId(status.getId());
            projectStatusDto.setStatusName(status.getStatusName());
            projectStatusDto.setDescription(status.getDescription());
            projectStatusDto.setActive(status.isActive());
            projectStatusDto.setCode(status.getCode());
            return projectStatusDto;
        }).toList();
    }
}
