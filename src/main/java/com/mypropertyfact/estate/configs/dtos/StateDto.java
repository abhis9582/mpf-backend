package com.mypropertyfact.estate.configs.dtos;

import lombok.Data;

@Data
public class StateDto {
    private int id;
    private int countryId;
    private String stateName;
    private String description;
    private String countryName;
    private int noOfCities;
}
