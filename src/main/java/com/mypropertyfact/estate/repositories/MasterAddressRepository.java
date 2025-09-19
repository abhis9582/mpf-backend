package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.MasterAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterAddressRepository extends JpaRepository<MasterAddress, Integer> {
}
