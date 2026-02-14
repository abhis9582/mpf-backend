package com.mypropertyfact.estate.dtos;

import lombok.Data;

@Data
public class FeatureDto {
    private Long id;
    private String title;
    private String description;
    private Boolean status;
}

