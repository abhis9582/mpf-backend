package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "project_banners")
@ToString(exclude = "project")
public class ProjectBanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String desktopBanner;
    private String mobileBanner;
    private String altTag;
    private String projectName;
    private String slugURL;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
