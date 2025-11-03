package com.mypropertyfact.estate.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OwnerTypeDto {
    private int ownerId;
    private String typeName;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
