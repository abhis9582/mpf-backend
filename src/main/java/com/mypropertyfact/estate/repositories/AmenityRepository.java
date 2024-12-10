package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
}
