package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {
    City findByName(String name);

    City findBySlugUrl(String url);
}
