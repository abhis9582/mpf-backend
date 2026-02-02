package com.mypropertyfact.estate.dtos;

import com.mypropertyfact.estate.configs.dtos.LocalityDto;
import lombok.Data;

import java.util.List;

@Data
public class CityDto {
    private int id;
    private int stateId;
    private int countryId;
    private int districtId;
    private String metaTitle;
    private String metaKeywords;
    private String metaDescription;
    private String cityName;
    private String stateName;
    private String countryName;
    private String cityDescription;
    private String cityImage;
    private String slugURL;
    private List<ProjectDetailDto> projectList;
    private List<LocalityDto> localityList;
}
