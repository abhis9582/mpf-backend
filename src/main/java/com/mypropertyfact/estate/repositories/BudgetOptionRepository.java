package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.BudgetOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetOptionRepository extends JpaRepository<BudgetOption, Integer> {
}
