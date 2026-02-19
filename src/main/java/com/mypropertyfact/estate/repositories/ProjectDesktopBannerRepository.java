package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectDesktopBanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectDesktopBannerRepository extends JpaRepository<ProjectDesktopBanner, Integer> {
    List<ProjectDesktopBanner> findByProject(Project project);
}
