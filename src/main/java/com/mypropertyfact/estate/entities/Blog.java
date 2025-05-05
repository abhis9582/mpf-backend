package com.mypropertyfact.estate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blogs")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Blog title is required")
    @Size(max = 255, message = "Blog title must not exceed 255 characters")
    @Column(name = "blog_title")
    private String blogTitle;
    @Column(name = "blog_keywords")
    @NotBlank(message = "Blog keywords is required")
    private String blogKeywords;
    @Column(name = "blog_meta_description", length = 500)
    @NotBlank(message = "Blog meta description is required")
    private String blogMetaDescription;
    @NotBlank(message = "Blog description is required")
    @Column(name = "blog_description", columnDefinition = "LONGTEXT")
    private String blogDescription;

    @NotBlank(message = "Slug URL is required")
    @Size(max = 255)
    @Column(name = "slug_url", unique = true)
    private String slugUrl;

    @Size(max = 255)
    @Column(name = "blog_image")
    private String blogImage;

    @Min(value = 0, message = "Status must be at least 0")
    @Max(value = 1, message = "Status must be at most 1")
    @Column(name = "status")
    private int status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_category_id")
    @JsonIgnore
    private BlogCategory blogCategory;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
