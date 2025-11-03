package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
    @Query("SELECT a FROM Amenity a LEFT JOIN FETCH a.projects WHERE a.id = :id")
    Optional<Amenity> findByIdWithProjects(@Param("id") int id);
    
    Optional<Amenity> findByTitleIgnoreCase(String title);
}
