package com.mypropertyfact.estate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectAmenityResponse {
    private int projectId;
    private String projectName;
    private String amenities;
}
