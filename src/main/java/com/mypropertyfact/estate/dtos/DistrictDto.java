package com.mypropertyfact.estate.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DistrictDto {
    private int id;
    private String name;
    private List<CityDto> cities;
    private String stateName;
}
