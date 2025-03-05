package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.AggregationFrom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregationFromRepository extends JpaRepository<AggregationFrom, Integer> {
}
