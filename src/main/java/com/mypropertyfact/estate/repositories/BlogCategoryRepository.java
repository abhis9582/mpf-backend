package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.BlogCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Integer> {

    @EntityGraph(attributePaths = "blogs")
    @Query("SELECT c FROM BlogCategory c")
    List<BlogCategory> findAllWithBlogs();
}
