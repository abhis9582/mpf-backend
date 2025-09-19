package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Integer> {
    Optional<State> findByStateName(String name);
}
