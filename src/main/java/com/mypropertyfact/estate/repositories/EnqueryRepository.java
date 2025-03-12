package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Enquery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnqueryRepository extends JpaRepository<Enquery, Integer> {
}
