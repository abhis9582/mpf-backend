package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.MasterBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface MasterBenefitRepository extends JpaRepository<MasterBenefit, Integer> {
    Optional<MasterBenefit> findByBenefitName(String name);
}
