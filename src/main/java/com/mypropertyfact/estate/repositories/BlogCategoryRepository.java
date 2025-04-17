package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Integer> {
}
