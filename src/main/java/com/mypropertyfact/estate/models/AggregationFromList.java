package com.mypropertyfact.estate.models;

import com.mypropertyfact.estate.entities.CityPriceDetail;
import com.mypropertyfact.estate.entities.TopDevelopersByValue;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class AggregationFromList {
    private String aggregationFrom;
    private String aggregationFromDisplayName;
    private String legendHeader;
    private List<CityPriceDetail> details = new ArrayList<>();
    private List<TopLocationsByTransactionMapper> locationDetails = new ArrayList<>();
    private List<TopDevelopersByValue> developerDetails = new ArrayList<>();
}
