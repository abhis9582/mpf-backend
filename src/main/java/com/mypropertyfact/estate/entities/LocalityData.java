package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "locality_data")
@Data
public class LocalityData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String localityId;

    @Lob
    private String localEconomyList;

    private int localEconomyScore;

    @Lob
    private String onGoingFutureProjectsList;

    private int onGoingFutureProjectsScore;

    @Lob
    private String connectivityAndCommuteList;

    private int connectivityAndCommuteScore;

    @Lob
    private String amenitiesAndGentrificationList;

    private int amenitiesAndGentrificationScore;

    @Lob
    private String trendsAndHistoricalDataList;

    private int trendsAndHistoricalDataScore;

    @Lob
    private String existingSupplyList;

    private int existingSupplyScore;

    @Lob
    private String interpretationAndOutlookList;

    @Lob
    private String recommendationsForInvestorsList;
}
