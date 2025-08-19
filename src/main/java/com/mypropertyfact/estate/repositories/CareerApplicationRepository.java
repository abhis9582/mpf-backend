package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.CareerApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerApplicationRepository extends JpaRepository<CareerApplication, Long> {
}
