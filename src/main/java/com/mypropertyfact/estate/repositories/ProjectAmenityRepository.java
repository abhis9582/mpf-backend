package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Amenity;
import com.mypropertyfact.estate.entities.ProjectAmenity;
import com.mypropertyfact.estate.models.ProjectAmenityResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectAmenityRepository extends JpaRepository<ProjectAmenity, Integer> {
    @Query("SELECT a FROM Amenity a WHERE id IN (SELECT amenityId FROM ProjectAmenity WHERE slugURL = :slugURL)")
    List<Amenity> findBySlugURL(@Param("slugURL")String slugURL);
}
