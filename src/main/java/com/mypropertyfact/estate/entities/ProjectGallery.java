package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ProjectGallery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int projectId;
    private String image;
    private String slugUrl;
    private String type;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
