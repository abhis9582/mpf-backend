package com.mypropertyfact.estate.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class ProjectStatusDto {
    private int id;
    @NotBlank
    private String statusName;
    private String code;
    @Size(max = 2000)
    private String description;
    private boolean isActive;
}
