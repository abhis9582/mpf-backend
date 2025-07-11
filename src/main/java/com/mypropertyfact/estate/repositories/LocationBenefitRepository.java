package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.LocationBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationBenefitRepository extends JpaRepository<LocationBenefit, Integer> {

    @Query("SELECT lb.id as id, lb.distance as distance, lb.benefitName as benefitName, lb.iconImage as image FROM " +
            "LocationBenefit lb")
    List<Object[]> getAllWithProjectName();
}
