package com.mypropertyfact.estate.dtos;

import lombok.Data;

/**
 * DTO for Nearby Benefit response
 * Used when returning property listing data with nearby benefits
 */
@Data
public class NearbyBenefitResponseDto {
    private Integer id;  // MasterBenefit ID
    private String benefitName;
    private String benefitIcon;
    private String altTag;
    private Double distance;  // Distance in KMs
}

