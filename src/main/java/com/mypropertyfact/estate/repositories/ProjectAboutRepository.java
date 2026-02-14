package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectsAbout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectAboutRepository extends JpaRepository<ProjectsAbout, Integer> {
    Optional<ProjectsAbout> findByProject_Id(int projectId);
}
