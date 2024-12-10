package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ProjectTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String projectTypeName;
    private String slugUrl;
    @Column(columnDefinition = "TEXT")
    private String projectTypeDesc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
