package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.projections.ProjectView;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project ,Integer> {
    List<ProjectView> findAllProjectedBy(Sort sort);

    Optional<Project> findBySlugURL(String url);

    @Query(value = "SELECT pa.project_id AS projectId, " +
            "p.project_name AS projectName, " +
            "GROUP_CONCAT(a.title ORDER BY a.title SEPARATOR ', ') AS amenities " +
            "FROM project_amenity pa " +
            "JOIN amenity a ON pa.amenity_id = a.id " +
            "JOIN projects p ON pa.project_id = p.id " +
            "GROUP BY pa.project_id, p.project_name",
            nativeQuery = true)
    List<Object[]> getAllProjectAmenity();
    @Query(value = """
                    select * from projects where property_type= :propertyType
                    and city_location = :propertyLocation
                    and project_price between :startBudget
                    and :endBudget
                    """, nativeQuery = true)
    List<Project> searchByPropertyTypeLocationBudget(@Param("propertyType") String propertyType, @Param("propertyLocation") String propertyLocation,
                                                     @Param("startBudget") int startBudget,
                                                     @Param("endBudget") int endBudget);

//    List<Project> fin
}
