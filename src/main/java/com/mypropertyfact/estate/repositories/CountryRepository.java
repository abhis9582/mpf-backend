package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    Optional<Country> findByCountryNameIgnoreCase(String countryName);
}
