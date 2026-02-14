package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.dtos.RoleDto;
import com.mypropertyfact.estate.entities.MasterRole;
import com.mypropertyfact.estate.interfaces.MasterRoleService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.MasterRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterRoleServiceImpl implements MasterRoleService {
    
    private final MasterRoleRepository masterRoleRepository;
    
    @Override
    public List<MasterRole> getAllRoles() {
        return masterRoleRepository.findAll();
    }
    
    @Override
    @Transactional
    public Response addUpdateRole(RoleDto roleDto) {
        try {
            MasterRole role;
            
            if (roleDto.getId() != null) {
                // Update existing role
                role = masterRoleRepository.findById(roleDto.getId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleDto.getId()));
                
                // Check if role name is being changed and if new name already exists
                if (!role.getRoleName().equalsIgnoreCase(roleDto.getRoleName())) {
                    Optional<MasterRole> existingRole = masterRoleRepository.findByRoleNameIgnoreCase(roleDto.getRoleName());
                    
                    if (existingRole.isPresent() && existingRole.get().getId() != roleDto.getId()) {
                        return new Response(0, "Role name already exists", 0);
                    }
                }
                
                role.setRoleName(roleDto.getRoleName());
                role.setDescription(roleDto.getDescription());
                if (roleDto.getIsActive() != null) {
                    role.setIsActive(roleDto.getIsActive());
                }
                
                log.info("Updating role: {}", roleDto.getRoleName());
            } else {
                // Create new role
                // Check if role name already exists
                Optional<MasterRole> existingRole = masterRoleRepository.findByRoleNameIgnoreCase(roleDto.getRoleName());
                
                if (existingRole.isPresent()) {
                    return new Response(0, "Role name already exists", 0);
                }
                
                role = new MasterRole();
                role.setRoleName(roleDto.getRoleName());
                role.setDescription(roleDto.getDescription());
                role.setIsActive(roleDto.getIsActive() != null ? roleDto.getIsActive() : true);
                
                log.info("Creating new role: {}", roleDto.getRoleName());
            }
            
            MasterRole savedRole = masterRoleRepository.save(role);
            
            return new Response(1, 
                roleDto.getId() != null ? "Role updated successfully" : "Role created successfully", 
                savedRole.getId());
                
        } catch (RuntimeException e) {
            log.error("Error saving role: {}", e.getMessage(), e);
            return new Response(0, e.getMessage(), 0);
        } catch (Exception e) {
            log.error("Unexpected error saving role: {}", e.getMessage(), e);
            return new Response(0, "Failed to save role: " + e.getMessage(), 0);
        }
    }
    
    @Override
    @Transactional
    public Response deleteRole(int id) {
        try {
            MasterRole role = masterRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
            
            // Check if role is being used by any users
            // Note: This is a simple check. In production, you might want to check user_roles table
            // For now, we'll just delete it. The database foreign key constraints will handle it.
            
            masterRoleRepository.delete(role);
            
            log.info("Deleted role with id: {}", id);
            return new Response(1, "Role deleted successfully", id);
            
        } catch (RuntimeException e) {
            log.error("Error deleting role: {}", e.getMessage(), e);
            return new Response(0, e.getMessage(), 0);
        } catch (Exception e) {
            log.error("Unexpected error deleting role: {}", e.getMessage(), e);
            return new Response(0, "Failed to delete role: " + e.getMessage(), 0);
        }
    }
}

