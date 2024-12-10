package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Data
@Table(name = "builders")
public class Builder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String builderName;
    private String slugUrl;
    @Column(columnDefinition = "TEXT")
    private String builderDesc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
