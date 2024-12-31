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

    @Query("SELECT p FROM Project p WHERE cityLocation = :cityName")
    List<Project> getAllByCity(@Param("cityName") String cityName);
    @Query(value = "SELECT pa.project_id AS projectId, " +
            "p.project_name AS projectName, " +
            "GROUP_CONCAT(a.title ORDER BY a.title SEPARATOR ', ') AS amenities " +
            "FROM project_amenity pa " +
            "JOIN amenity a ON pa.amenity_id = a.id " +
            "JOIN projects p ON pa.project_id = p.id " +
            "GROUP BY pa.project_id, p.project_name",
            nativeQuery = true)
    List<Object[]> getAllProjectAmenity();
}
