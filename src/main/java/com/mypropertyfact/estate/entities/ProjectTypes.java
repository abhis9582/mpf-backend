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
    private String metaTitle;
    @Lob
    private String metaKeyword;
    @Lob
    private String metaDesc;
    @Lob
    private String projectTypeDesc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
