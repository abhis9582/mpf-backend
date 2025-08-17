package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.ProjectDesktopBannerDto;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface ProjectDesktopBannerService {
    Response addUpdateDesktopBanner(ProjectDesktopBannerDto projectDesktopBannerDto);
    Response deleteDesktopBanner(int id);
    List<ProjectDesktopBannerDto> getAllDesktopBannerList();
}
