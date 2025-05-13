package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.BlogContentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogContentImageRepository extends JpaRepository<BlogContentImage, Integer> {

}
