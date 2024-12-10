package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ProjectFaqs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int projectId;
    @Column(columnDefinition = "TEXT")
    private String faqQuestion;
    @Column(columnDefinition = "TEXT")
    private String faqAnswer;
    private String slugUrl;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
