package com.mypropertyfact.estate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for storing property listing images.
 * Each PropertyListing can have multiple images.
 */
@Entity
@Table(name = "property_listing_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "propertyListing")
public class PropertyListingImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;
    
    @Column(name = "image_name", length = 255)
    private String imageName;
    
    @Column(name = "image_size")
    private Long imageSize; // Size in bytes
    
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0; // For ordering images
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false; // Primary/featured image
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_listing_id", nullable = false)
    @JsonIgnore
    private PropertyListing propertyListing;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

