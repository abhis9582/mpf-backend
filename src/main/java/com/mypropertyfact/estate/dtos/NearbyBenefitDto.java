package com.mypropertyfact.estate.dtos;

import lombok.Data;

/**
 * DTO for Nearby Benefit with distance
 * Used when submitting property listings
 */
@Data
public class NearbyBenefitDto {
    private Integer id;  // MasterBenefit ID
    private Double distance;  // Distance in KMs
}

