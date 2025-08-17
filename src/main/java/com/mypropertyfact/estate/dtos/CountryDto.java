package com.mypropertyfact.estate.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CountryDto {
    private int id;
    private String countryName;
    private String countryDescription;
    private String continent;
    private List<CityDto> cityList;
    private List<StateDto> stateList;
}
