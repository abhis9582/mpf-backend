package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.dtos.ProjectMobileBannerDto;
import com.mypropertyfact.estate.interfaces.ProjectMobileBannerService;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public class ProjectMobileBannerServiceImpl implements ProjectMobileBannerService {
    @Override
    public Response addUpdateMobileBanner(ProjectMobileBannerDto projectMobileBannerDto) {
        return null;
    }

    @Override
    public Response deleteMobileBanner(int id) {
        return null;
    }

    @Override
    public List<ProjectMobileBannerDto> getAllProjectMobileBannerList() {
        return List.of();
    }
}
