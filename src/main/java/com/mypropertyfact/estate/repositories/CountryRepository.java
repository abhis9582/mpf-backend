package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
}
