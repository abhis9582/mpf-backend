package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectWalkthrough;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectWalkthroughRepository extends JpaRepository<ProjectWalkthrough, Integer> {
    ProjectWalkthrough findBySlugURL(String url);
}
