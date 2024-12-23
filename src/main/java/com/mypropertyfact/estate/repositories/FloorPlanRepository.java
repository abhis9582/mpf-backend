package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.FloorPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorPlanRepository extends JpaRepository<FloorPlan, Integer> {
    @Query("SELECT p.projectName as pName, fp.planType as type, fp.areaSqft as areaSq, fp.areaSqmt as areaMt " +
            "FROM Project p INNER JOIN FloorPlan fp ON p.id = fp.projectId")
    List<Object[]> getAllFloorPlans();

    List<FloorPlan> findBySlugUrl(String url);
}
