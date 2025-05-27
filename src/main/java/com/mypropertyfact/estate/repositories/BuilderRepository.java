package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Builder;
import com.mypropertyfact.estate.projections.BuilderView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuilderRepository extends JpaRepository<Builder, Integer> {
    Builder findByBuilderName(String builderName);

    Builder findBySlugUrl(String url);
    List<BuilderView> findAllProjectedBy();
}
