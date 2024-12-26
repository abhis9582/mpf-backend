package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectBannerRepository extends JpaRepository<ProjectBanner, Integer> {
    @Query("SELECT p FROM ProjectBanner p WHERE slugURL= :slugURL")
    ProjectBanner getBySlug(@Param("slugURL") String slugURL);
}
