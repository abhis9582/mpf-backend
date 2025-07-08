package com.mypropertyfact.estate.configs.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalityDataResponse {
    private long id;
    private String localityId;
    private String localityName;
    private List<String> localEconomyList;
    private int localEconomyScore;

    private List<String> onGoingFutureProjectsList;
    private int onGoingFutureProjectsScore;

    private List<String> connectivityAndCommuteList;
    private int connectivityAndCommuteScore;

    private List<String> amenitiesAndGentrificationList;
    private int amenitiesAndGentrificationScore;

    private List<String> trendsAndHostoricalDataList;
    private int trendsAndHistoricalDataScore;

    private List<String> exestingSupplyList;
    private int existingSupplyScore;

    private List<String> interpretationAndOutlookList;
    private List<String> recommendationsForInvestorsList;
}
