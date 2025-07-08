package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.LocalityData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LocalityDataRepository extends JpaRepository<LocalityData, Long> {
    Optional<LocalityData> findByLocalityId(String localityId);
}
