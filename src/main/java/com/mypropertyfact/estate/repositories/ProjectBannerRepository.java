package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.ProjectBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectBannerRepository extends JpaRepository<ProjectBanner, Integer> {
    @Query("SELECT pb FROM ProjectBanner pb WHERE pb.type = 'desktop'")
    List<ProjectBanner> getAllDesktopBanners();

    @Query("SELECT pb FROM ProjectBanner pb WHERE pb.type = 'mobile'")
    List<ProjectBanner> getAllMobileBanners();
}
