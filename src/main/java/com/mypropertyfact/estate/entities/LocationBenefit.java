package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "location_benefits")
public class LocationBenefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int projectId;
    private String iconImage;
    private String benefitName;
    private String distance;
    private String slugUrl;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

}
