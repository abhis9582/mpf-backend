package com.mypropertyfact.estate.models;

import com.mypropertyfact.estate.entities.Amenity;
import lombok.Data;

import java.util.List;

@Data
public class ProjectAmenityDto {
    private int id;
    private int projectId;
    private List<Amenity> amenityList;
    private String slugURL;
}
