package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

//@Entity
//@Data
//@Table(name = "micro_market")
public class MicroMarket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int transactions;
    private int noOfProjects;
    private int currentPrice;
    private int change;
    @ManyToOne
    private City city;
}
