package com.mypropertyfact.estate.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String altTag;
    private String amenityImageUrl;
    private boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
