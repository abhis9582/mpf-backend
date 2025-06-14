package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Integer> {
}
