package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for storing nearby benefits with distances for property listings
 * Links PropertyListing to MasterBenefit with distance information
 */
@Entity
@Table(name = "property_listing_nearby_benefits")
@Data
@ToString(exclude = {"propertyListing", "masterBenefit"})
public class PropertyListingNearbyBenefit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "distance", nullable = false)
    private Double distance; // Distance in KMs
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_listing_id", nullable = false)
    private PropertyListing propertyListing;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_benefit_id", nullable = false)
    private MasterBenefit masterBenefit;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

