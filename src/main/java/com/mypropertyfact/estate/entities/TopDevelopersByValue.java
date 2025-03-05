package com.mypropertyfact.estate.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopDevelopersByValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String developerName;
    private String location;
    private String locality;
    private String noOfTransactions;
    private String priority;
    private String saleRentValue;
    private String city;
    private int aggregationFrom;
    private int categoryId;
}

