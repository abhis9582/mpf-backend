package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.FloorPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorPlanRepository extends JpaRepository<FloorPlan, Integer> {

}
