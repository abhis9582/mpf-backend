package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
