package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.projections.CityView;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
    City findByName(String name);

    Optional<City> findBySlugUrl(String url);

    List<CityView> findAllProjectedBy(Sort sort);
}
