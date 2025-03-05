package com.mypropertyfact.estate.models;

import lombok.Data;

@Data
public class TopLocationsByTransactionMapper {
    private String location;
    private String city;
    private String transactions;
    private String currentPrice;
    private int saleValue;
}
