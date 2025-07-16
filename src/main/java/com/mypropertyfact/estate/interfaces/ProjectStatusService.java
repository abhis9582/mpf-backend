package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.configs.dtos.ProjectStatusDto;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface ProjectStatusService {

    Response addUpdate(ProjectStatusDto projectStatusDto);
    Response deleteProjectStatus(int id);
    List<ProjectStatusDto> getAllStatus();
}
