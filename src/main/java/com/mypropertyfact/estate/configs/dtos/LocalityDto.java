package com.mypropertyfact.estate.configs.dtos;

import lombok.Data;


@Data
public class LocalityDto {
    private Long id;
    private String localityName;
    private String slug;
    private Double latitude;
    private Double longitude;
    private Integer pinCode;
    private String description;
    private Double averagePricePerSqFt;
    private Boolean isActive;
    private int cityId;
    private String cityName;
    private String stateName;
}
