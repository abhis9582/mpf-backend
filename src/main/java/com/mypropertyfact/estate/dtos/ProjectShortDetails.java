package com.mypropertyfact.estate.dtos;

import lombok.Data;

@Data
public class ProjectShortDetails {
    private int id;
    private String projectName;
    private String projectPrice;
    private String slugURL;
    private String projectLocality;
    private String projectConfiguration;
    private boolean status;
    private String builderName;
    private String projectStatusName;
    private String propertyTypeName;
    private String cityName;
    private String projectAddress;
    private String projectThumbnailImage;
    private String floorTypes;
}
