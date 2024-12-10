package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.configs.dtos.FaqResponse;
import com.mypropertyfact.estate.entities.ProjectFaqs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectFaqsRepository extends JpaRepository<ProjectFaqs, Integer> {
    @Query("SELECT p.projectName as projectName, fq.faqQuestion as question, fq.faqAnswer as answer FROM " +
            " Property p INNER JOIN ProjectFaqs fq ON p.id = fq.projectId")
    List<Object[]> getAllWithProjectName();

    List<ProjectFaqs> findBySlugUrl(String url);
}
