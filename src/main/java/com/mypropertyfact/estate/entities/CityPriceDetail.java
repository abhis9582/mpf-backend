package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "price_detail")
public class CityPriceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String changePercentage;
    private String changeValue;
    private String currentRate;
    private String location;
    private String locality;
    private String locationUrl;
    private String noOfProjects;
    private String noOfTransactions;
    private String priority;
    private String city;
    private String saleRentValue;
    private int aggregationFromId;
    private int categoryId;
}
