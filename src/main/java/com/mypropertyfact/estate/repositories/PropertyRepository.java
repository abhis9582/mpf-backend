package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.configs.dtos.ProjectWithBannerDTO;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Integer> {
    Property findBySlugURL(String slugUrl);
    @Query("SELECT p.id, p.projectName AS projectName, p.projectPrice AS price, p.projectLocality AS location, MAX(pb.mobileBanner) AS image, p.slugURL as slugURL " +
            "FROM Property p JOIN ProjectBanner pb ON p.id = pb.projectId  " +
            "WHERE pb.type = 'mobile' GROUP BY p.id")
    List<Object[]> getAllProjectsWithDesktopBanners();
    @Query("SELECT p FROM Project p "+
            "WHERE p.cityLocation = :cityName ")
    List<Project> getAllByCity(@Param("cityName") String cityName);
}
