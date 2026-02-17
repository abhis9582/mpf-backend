package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.dtos.ProjectShortDetails;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.projections.ProjectView;

import lombok.RequiredArgsConstructor;

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
    @Query("SELECT p FROM Project p WHERE p.slugURL = :url AND p.status = true")
    Optional<Project> findBySlugURLWithAllRelations(@Param("url") String url);

    @EntityGraph(value = "Project.withAllRelations", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM Project p WHERE p.slugURL = :url")
    Optional<Project> findBySlugURLWithAllRelationsNoFilter(@Param("url") String url);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.floorPlans ORDER BY p.projectName")
    List<Project> findAllWithFloorPlans();

    @Query("""
            SELECT p FROM Project p
            WHERE p.status = true
            AND (:type IS NULL OR p.projectTypes.id = :type)
            AND (:city IS NULL OR p.city.id = :city)
            AND (CAST(p.projectPrice AS float) BETWEEN :start AND :end)
            """)
    List<Project> searchProjects(
            @Param("type") Integer type,
            @Param("city") Integer city,
            @Param("start") float start,
            @Param("end") float end
    );

    @Query("""
            SELECT p FROM Project p
            JOIN FETCH p.projectStatus s
            WHERE (:typeId IS NULL OR p.projectTypes.id = :typeId)
            AND (:newLaunch = false OR s.statusName = 'New Launched')
            ORDER BY p.projectName ASC
            """)
    List<Project> findProjectsByType(Integer typeId, boolean newLaunch);

    @Query("""
            SELECT new com.mypropertyfact.estate.dtos.ProjectShortDetails(
                p.id,
                p.projectName,
                p.projectPrice,
                p.slugURL,
                p.projectLocality,
                p.projectConfiguration,
                p.status,
                b.builderName,
                ps.statusName,
                pt.projectTypeName,
                c.name,
                CONCAT(p.projectLocality, ', ', c.name),
                p.projectThumbnail,
                p.projectLogo,
                pdb.desktopImage,
                b.slugUrl
            )
            FROM Project p
            LEFT JOIN p.projectStatus ps
            LEFT JOIN p.city c
            LEFT JOIN p.builder b
            LEFT JOIN p.projectDesktopBanners pdb
            LEFT JOIN p.projectTypes pt
            WHERE
                (pdb.id IS NULL OR pdb.id = (
                    SELECT MIN(pdb2.id)
                    FROM ProjectDesktopBanner pdb2
                    WHERE pdb2.project = p
                ))
            """)
    List<ProjectShortDetails> findAllProjectedBy();

}
