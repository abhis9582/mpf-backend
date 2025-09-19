package com.mypropertyfact.estate.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "master_roles")
public class MasterRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 100)
    private String roleName;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "roles")
    private Set<MasterOwner> masterOwners = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    private Set<MasterBroker> masterBrokers = new HashSet<>();
}
