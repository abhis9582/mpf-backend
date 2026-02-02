package com.mypropertyfact.estate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "zones")
@Data
@ToString(exclude = {"city", "localities"})
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String zoneName;

    @Column(unique = true, nullable = false)
    private String slug;

    private String description;

    private boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    @JsonIgnore
    private City city;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Locality> localities;
}
