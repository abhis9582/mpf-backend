package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByTitleIgnoreCase(String title);
    
    List<Feature> findByStatusTrueOrderByTitleAsc();
    
    @Query("SELECT f FROM Feature f ORDER BY f.title ASC")
    List<Feature> findAllOrderByTitleAsc();
}

