package com.mypropertyfact.estate.models;

import com.mypropertyfact.estate.entities.Headers;
import lombok.Data;

import java.util.List;
@Data
public class TopDevelopersByValueResponse {
    private String category;
    private String categoryDisplayName;
    private List<Headers> headers;
    private List<AggregationFromList> aggregationFromList;
}
