package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectsAbout;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectAboutRepository extends JpaRepository<ProjectsAbout, Integer> {
    Optional<ProjectsAbout> findByProjectId(int projectId);
}
