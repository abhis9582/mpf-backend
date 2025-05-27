package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectTypes;
import com.mypropertyfact.estate.projections.ProjectTypeView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTypeRepository extends JpaRepository<ProjectTypes, Integer> {
    ProjectTypes findByProjectTypeName(String projectTypeName);

    ProjectTypes findBySlugUrl(String url);

    List<ProjectTypeView> findAllProjectedBy();
}
