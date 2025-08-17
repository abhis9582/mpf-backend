package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.dtos.ProjectDesktopBannerDto;
import com.mypropertyfact.estate.interfaces.ProjectDesktopBannerService;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public class ProjectDesktopBannerServiceImpl implements ProjectDesktopBannerService {
    @Override
    public Response addUpdateDesktopBanner(ProjectDesktopBannerDto projectDesktopBannerDto) {
        return null;
    }

    @Override
    public Response deleteDesktopBanner(int id) {
        return null;
    }

    @Override
    public List<ProjectDesktopBannerDto> getAllDesktopBannerList() {
        return List.of();
    }
}
