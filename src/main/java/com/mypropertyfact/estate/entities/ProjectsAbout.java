package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@ToString(exclude = "project")
public class ProjectsAbout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Lob
    private String shortDesc;
    @Lob
    private String longDesc;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "project_id", unique = true)
    private Project project;

    @PrePersist
    public void onCreate() {
        this.createdAt= LocalDateTime.now();
        this.updatedAt= LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt= LocalDateTime.now();
    }
}
