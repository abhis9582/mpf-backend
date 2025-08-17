package com.mypropertyfact.estate.dtos;

import lombok.Data;

import java.util.List;

@Data
public class StateDto {
    private int id;
    private int countryId;
    private String countryName;
    private String countryDescription;
    private String stateName;
    private String stateDescription;
    private List<CityDto> cityList;
}
