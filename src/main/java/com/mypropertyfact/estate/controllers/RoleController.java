package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.dtos.RoleDto;
import com.mypropertyfact.estate.entities.MasterRole;
import com.mypropertyfact.estate.interfaces.MasterRoleService;
import com.mypropertyfact.estate.models.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Role Management
 * Only users with SUPERADMIN role can access these endpoints
 */
@RestController
@RequestMapping("/api/v1/admin/roles")
@Slf4j
@RequiredArgsConstructor
public class RoleController {
    
    private final MasterRoleService masterRoleService;

    /**
     * Get all roles
     * GET /api/admin/roles
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> getAllRoles() {
        try {
            List<MasterRole> roles = masterRoleService.getAllRoles();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", roles.size());
            response.put("roles", roles);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching roles: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch roles: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get role by ID
     * GET /api/admin/roles/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> getRoleById(@PathVariable Integer id) {
        try {
            List<MasterRole> allRoles = masterRoleService.getAllRoles();
            MasterRole role = allRoles.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
            
            if (role == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Role not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("role", role);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching role: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch role: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Create a new role
     * POST /api/admin/roles
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> createRole(@RequestBody @Valid RoleDto roleDto) {
        try {
            // Ensure ID is null for new roles
            roleDto.setId(null);
            
            Response response = masterRoleService.addUpdateRole(roleDto);
            
            if (response.getIsSuccess() == 1) {
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", response.getMessage());
                successResponse.put("roleId", response.getProjectId());
                
                return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", response.getMessage());
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("Error creating role: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create role: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Update an existing role
     * PUT /api/admin/roles/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody @Valid RoleDto roleDto) {
        try {
            roleDto.setId(id);
            
            Response response = masterRoleService.addUpdateRole(roleDto);
            
            if (response.getIsSuccess() == 1) {
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", response.getMessage());
                successResponse.put("roleId", response.getProjectId());
                
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", response.getMessage());
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("Error updating role: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update role: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Delete a role
     * DELETE /api/admin/roles/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        try {
            Response response = masterRoleService.deleteRole(id);
            
            if (response.getIsSuccess() == 1) {
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", response.getMessage());
                
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", response.getMessage());
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("Error deleting role: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to delete role: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

