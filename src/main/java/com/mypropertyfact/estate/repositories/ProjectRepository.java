package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project ,Integer> {

    Project findBySlugURL(String url);
    @Query("SELECT p FROM Project p WHERE projectBy = :builderId")
    List<Project> getAllBuilderProjects(@Param("builderId")int builderId);
    @Query("SELECT p FROM Project p WHERE propertyType = :id")
    List<Project> getAllProjectsByType(@Param("id") int id);
}
