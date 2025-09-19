package com.mypropertyfact.estate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "master_addresses")
@ToString(exclude = {"masterBroker", "masterOwner"})
public class MasterAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String flatNo;          // Apartment/Flat/House number
    private String buildingName;    // Building/Complex/Society name
    private String streetAddress;   // Street / Area / Locality
    private String landmark;        // Optional nearby landmark
    private String pinCode;
    private Boolean isPrimary;
    private Boolean isActive = true;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "broker_id")
    private MasterBroker masterBroker;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "owner_id")
    private MasterOwner masterOwner;
}
