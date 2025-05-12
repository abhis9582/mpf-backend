package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "blog_content_image")
public class BlogContentImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "image")
    private String image;
    @NotBlank(message = "Alt tag is required")
    @Column(name = "alt_tag")
    private String altTag;
    @NotNull(message = "width is required !")
    @Column(name = "width")
    private int imageWidth;

    @NotNull(message = "height is required !")
    @Column(name = "height")
    private int imageHeight;

    @Column(name = "url")
    private String imageUrl;
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void createdOn(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updatedOn() {
        this.updatedAt = LocalDateTime.now();
    }
}
