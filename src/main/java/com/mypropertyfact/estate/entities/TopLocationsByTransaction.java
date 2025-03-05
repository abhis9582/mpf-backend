package com.mypropertyfact.estate.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TopLocationsByTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String location;
    private String city;
    private int transactions;
    private int currentPrice;
    private int saleValue;
    private int aggregationFrom;
    private int categoryId;
}
