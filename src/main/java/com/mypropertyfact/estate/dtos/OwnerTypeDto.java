package com.mypropertyfact.estate.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OwnerTypeDto {
    private int ownerId;
    private String typeName;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
