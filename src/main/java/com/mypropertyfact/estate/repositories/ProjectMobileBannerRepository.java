package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectMobileBanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMobileBannerRepository extends JpaRepository<ProjectMobileBanner, Integer> {
    List<ProjectMobileBanner> findByProjectId(int projectId);
}
