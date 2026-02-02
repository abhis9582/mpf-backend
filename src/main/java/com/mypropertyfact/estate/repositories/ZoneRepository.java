package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByZoneNameAndCityId(String zoneName, Integer cityId);
    Optional<Zone> findBySlug(String slug);
}
