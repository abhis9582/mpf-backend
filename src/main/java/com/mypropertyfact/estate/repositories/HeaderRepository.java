package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.Headers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HeaderRepository extends JpaRepository<Headers, Integer> {
    @Query(value = """
            SELECT * FROM headers h
            WHERE h.id IN (5, 2, 3, 4)
            ORDER BY FIELD(h.id, 5, 2, 3, 4);
            """, nativeQuery = true)
    List<Headers> getAll();

    @Query(value = """
            SELECT * FROM headers h
            WHERE h.id IN (5, 2, 3, 6)
            ORDER BY FIELD(h.id, 5, 2, 3, 6);
            """, nativeQuery = true)
    List<Headers> getTopLocationsByTransactionHeaders();
    @Query(value = """
            SELECT * FROM headers 
            as h WHERE id IN 
            (7, 2, 6) ORDER BY 
            FIELD(h.id, 7, 2, 6);
            """, nativeQuery = true)
    List<Headers> getTopDevelopersHeaders();
}
