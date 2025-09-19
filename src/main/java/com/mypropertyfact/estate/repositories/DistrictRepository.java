package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    Optional<District> findByName(String name);
}
