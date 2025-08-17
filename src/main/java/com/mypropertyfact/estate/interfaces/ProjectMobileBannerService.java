package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.ProjectMobileBannerDto;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface ProjectMobileBannerService {
    Response addUpdateMobileBanner(ProjectMobileBannerDto projectMobileBannerDto);
    Response deleteMobileBanner(int id);
    List<ProjectMobileBannerDto> getAllProjectMobileBannerList();
}
