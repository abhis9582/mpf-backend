package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Integer> {
}
