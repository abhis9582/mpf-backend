package com.mypropertyfact.estate.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IndianData {
    @JsonProperty("circlename")
    private String circleName;

    @JsonProperty("regionname")
    private String regionName;

    @JsonProperty("divisionname")
    private String divisionName;

    @JsonProperty("officename")
    private String officeName;

    @JsonProperty("pincode")
    private String pinCode;

    @JsonProperty("officetype")
    private String officeType;

    @JsonProperty("delivery")
    private String delivery;

    @JsonProperty("district")
    private String district;

    @JsonProperty("statename")
    private String stateName;

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("longitude")
    private String longitude;
}
