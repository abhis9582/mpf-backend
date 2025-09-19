package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.MasterOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterOwnerRepository extends JpaRepository<MasterOwner, Integer> {
}
