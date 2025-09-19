package com.mypropertyfact.estate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mypropertyfact.estate.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "districts")
@Data
@ToString(exclude = {"cities", "state"})
public class District implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // Official name of the district
    @Column(nullable = false, length = 150)
    private String name;
    // Headquarters city/town of the district
    @OneToMany(mappedBy = "district")
    @JsonIgnore
    private List<City> cities;
    // State or Union Territory this district belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;
    // Status (active/inactive)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status = Status.ACTIVE;
}
