package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.PropertyListingNearbyBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyListingNearbyBenefitRepository extends JpaRepository<PropertyListingNearbyBenefit, Long> {
    List<PropertyListingNearbyBenefit> findByPropertyListingId(Long propertyListingId);
    void deleteByPropertyListingId(Long propertyListingId);
}

