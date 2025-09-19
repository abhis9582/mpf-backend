package com.mypropertyfact.estate.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FirmTypeDto {
    private int firmId;

    @NotBlank(message = "Firm type is required")
    @Size(max = 100, message = "Firm type cannot exceed 100 characters")
    private String type;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
