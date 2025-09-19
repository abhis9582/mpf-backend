package com.mypropertyfact.estate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "master_owners")
public class MasterOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String ownerFullName;
    private String ownerMobileNumber;
    private String ownerEmail;
    private Boolean isAcceptedTermAndCondition;
    private String entityLegalName;
    private String authorizedSignatoryNameAndEmail;
    private String gstInNumber;
    private Boolean isActive;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "owner_roles",
            joinColumns = @JoinColumn(name = "owner_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<MasterRole> roles = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City ownerCity;

    @OneToMany(mappedBy = "masterOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MasterAddress> ownerRegisteredAddresses;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private MasterOwnerType ownerType;
}
