package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectGalleryRepository extends JpaRepository<ProjectGallery, Integer> {
    @Query("SELECT pg.id, p.projectName as pName, pg.image as image, p.slugURL as slugURL FROM Project p INNER JOIN " +
            " ProjectGallery pg on p.id = pg.projectId")
    List<Object[]> getAllGalleyImages();

    List<ProjectGallery> findBySlugUrl(String url);
}
