package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "city")
@Data
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String state;
    private String slugUrl;
    @Column(columnDefinition = "TEXT")
    private String cityDisc;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
