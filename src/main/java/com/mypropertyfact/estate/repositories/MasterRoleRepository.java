package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.MasterRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterRoleRepository extends JpaRepository<MasterRole, Integer> {
    Optional<MasterRole> findByRoleNameIgnoreCase(String roleName);
}
