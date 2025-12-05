package com.mypropertyfact.estate.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Integer id; // For updates, null for new roles
    
    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters")
    private String roleName;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    private Boolean isActive = true;
}
