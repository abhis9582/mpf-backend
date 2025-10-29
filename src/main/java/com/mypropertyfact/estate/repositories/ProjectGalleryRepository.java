package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectGalleryRepository extends JpaRepository<ProjectGallery, Integer> {
    List<ProjectGallery> findBySlugUrl(String url);
}
