package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.projections.ProjectView;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
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
            and city_id = :propertyLocation
            and project_price between :startBudget
            and :endBudget
            """, nativeQuery = true)
    List<Project> searchByPropertyTypeLocationBudget(@Param("propertyType") String propertyType, @Param("propertyLocation") String propertyLocation,
                                                     @Param("startBudget") int startBudget,
                                                     @Param("endBudget") int endBudget);

    //    List<Project> fin
    List<Project> findByStatusTrue(Sort sort);

    @EntityGraph(value = "Project.withAllRelations", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM Project p WHERE p.status = true ORDER BY p.projectName")
    List<Project> findAllWithAllRelations();

    @EntityGraph(value = "Project.withAllRelations", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM Project p WHERE p.slugURL = :url AND p.status= true")
    Optional<Project> findBySlugURLWithAllRelations(@Param("url") String url);

    List<Project> findByStatusTrueOrderByProjectNameAsc();
    
    // ========== New methods for user property submission ==========
    
    /**
     * Find projects submitted by a specific user
     */
    List<Project> findBySubmittedById(Integer userId);
    
    /**
     * Find projects by user and approval status
     */
    List<Project> findBySubmittedByIdAndApprovalStatus(Integer userId, com.mypropertyfact.estate.enums.ProjectApprovalStatus approvalStatus);
    
    /**
     * Find projects by approval status (for admin)
     */
    List<Project> findByApprovalStatus(com.mypropertyfact.estate.enums.ProjectApprovalStatus approvalStatus);

}
