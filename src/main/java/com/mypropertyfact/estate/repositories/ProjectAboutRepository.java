package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectsAbout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public interface ProjectAboutRepository extends JpaRepository<ProjectsAbout, Integer> {

    ProjectsAbout findBySlugURL(String url);
    @Query("SELECT pa.id, pa.projectId, p.projectName, pa.shortDesc, pa.longDesc FROM ProjectsAbout pa LEFT JOIN " +
            "Project p ON  p.id = pa.projectId")
    List<Object[]> getAllProjectAbout();
}
