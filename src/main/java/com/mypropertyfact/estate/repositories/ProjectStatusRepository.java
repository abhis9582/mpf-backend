package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Integer> {
    Optional<ProjectStatus> findByStatusNameIgnoreCase(String statusName);
}
