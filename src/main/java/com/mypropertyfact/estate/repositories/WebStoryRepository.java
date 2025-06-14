package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.WebStory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebStoryRepository extends JpaRepository<WebStory, Integer> {
}
