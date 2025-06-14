package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.WebStoryCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebStoryCategoryRepository extends JpaRepository<WebStoryCategory, Integer> {
    Optional<WebStoryCategory> findByCategoryName(String slug);
}
