package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "project_banners")
public class ProjectBanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int projectId;
    private String desktopBanner;
    private String mobileBanner;
    private String type;
    private String projectName;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
