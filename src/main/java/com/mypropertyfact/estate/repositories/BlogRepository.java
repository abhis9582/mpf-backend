package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

    // To check if a blog with the given slug exists
    boolean existsBySlugUrl(String slugUrl);

    // To fetch a blog by its slug URL
    Blog findBySlugUrl(String slugUrl);

}
