package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocalityRepository extends JpaRepository<Locality, Long> {

    @Query("SELECT l FROM Locality l JOIN FETCH l.city")
    List<Locality> findAllWithCity();
}
