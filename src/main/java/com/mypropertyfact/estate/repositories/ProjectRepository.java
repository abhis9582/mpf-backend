package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.projections.ProjectView;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query("SELECT p FROM Project p WHERE p.slugURL = :url AND (p.status = true )")
    Optional<Project> findBySlugURLWithAllRelations(@Param("url") String url);
    
    @EntityGraph(value = "Project.withAllRelations", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM Project p WHERE p.slugURL = :url")
    Optional<Project> findBySlugURLWithAllRelationsNoFilter(@Param("url") String url);

    @EntityGraph(value = "Project.withAllRelations", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM Project p WHERE p.id = :id")
    Optional<Project> findByIdWithAllRelations(@Param("id") Integer id);

    List<Project> findByStatusTrueOrderByProjectNameAsc();
    
    // ========== New methods for user property submission ==========
    
    /**
     * Find projects submitted by a specific user
     */
//     List<Project> findBySubmittedById(Integer userId);
    
    /**
     * Find projects by user and approval status
     */
//     List<Project> findBySubmittedByIdAndApprovalStatus(Integer userId, com.mypropertyfact.estate.enums.ProjectApprovalStatus approvalStatus);
    
    /**
     * Find projects by approval status (for admin)
     */
//     List<Project> findByApprovalStatus(com.mypropertyfact.estate.enums.ProjectApprovalStatus approvalStatus);

    /**
     * Find all projects with their floor plans loaded (avoids N+1 queries)
     */
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.floorPlans ORDER BY p.projectName")
    List<Project> findAllWithFloorPlans();

    /**
     * Find all projects with only essential relations for ProjectInfoDto (optimized for getAllProjects)
     * Uses EntityGraph to eagerly fetch only builder, projectTypes, projectStatus, and city
     */
    @EntityGraph(attributePaths = {"builder", "projectTypes", "projectStatus", "city"})
    @Query("SELECT p FROM Project p ORDER BY p.projectName ASC")
    List<Project> findAllForProjectInfo(Sort sort);

    /**
     * Find all projects with pagination and essential relations for ProjectInfoDto
     * Uses EntityGraph to eagerly fetch only builder, projectTypes, projectStatus, and city
     * Sorting is handled by Pageable parameter
     */
    @EntityGraph(attributePaths = {"builder", "projectTypes", "projectStatus", "city"})
    @Query("SELECT p FROM Project p")
    Page<Project> findAllForProjectInfo(Pageable pageable);

}
