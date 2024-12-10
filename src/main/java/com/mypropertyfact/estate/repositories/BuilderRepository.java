package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Builder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuilderRepository extends JpaRepository<Builder, Integer> {
    Builder findByBuilderName(String builderName);

    Builder findBySlugUrl(String url);
}
