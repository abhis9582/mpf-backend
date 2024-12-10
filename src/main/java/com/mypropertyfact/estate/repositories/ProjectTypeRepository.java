package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTypeRepository extends JpaRepository<ProjectTypes, Integer> {
    ProjectTypes findByProjectTypeName(String projectTypeName);

    ProjectTypes findBySlugUrl(String url);
}
