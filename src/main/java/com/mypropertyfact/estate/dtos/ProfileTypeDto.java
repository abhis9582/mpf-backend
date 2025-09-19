package com.mypropertyfact.estate.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileTypeDto {
    private int id;
    private String profileType;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
