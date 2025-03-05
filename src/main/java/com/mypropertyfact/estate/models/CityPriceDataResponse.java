package com.mypropertyfact.estate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityPriceDataResponse {
    private int id;
    private String changePercentage;
    private String changeValue;
    private String cityName;
    private String currentRate;
    private String locationUrl;
    private String noOfProjects;
    private String noOfTransactions;
    private String aggregationFrom;
    private String category;
}
