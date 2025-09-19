package com.mypropertyfact.estate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "master_brokers")     // "firmRegisteredAddresses"
@ToString(exclude = {"profileType", "reraState", "roles", "ownerType", "firmType", "brokerAddresses", "brokerCity"})
public class MasterBroker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String brokerFullName;
    private String brokerMobileNumber;
    private String brokerEmail;
    private String reraAgentRegistrationNumber;
    private String panNumber;
    private String companyAssociation;
    private Boolean isWhatsAppEnabled;
    private Boolean isTermAndConditionAccepted;
    private Boolean isActive;
    private String legalEntityName;
    private String gstInNumber;
    private String cinOrLLpin;
    private String authorizedSignatoryNameAndEmail;
    private String supportNumber;
    private Boolean isAcceptedTermAndCondition;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_type_id")
    private MasterProfileType profileType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rera_state_id")
    private State reraState;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City brokerCity;

    @JsonIgnore
    @OneToMany(mappedBy = "masterBroker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MasterAddress> brokerAddresses;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_type_id")
    private MasterFirmType firmType;

//    @JsonIgnore
//    @OneToMany(mappedBy = "")
//    private List<MasterAddress> firmRegisteredAddresses;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private MasterOwnerType ownerType;

    // Many-to-Many with Role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "broker_roles",
            joinColumns = @JoinColumn(name = "broker_id"), // foreign key in join table
            inverseJoinColumns = @JoinColumn(name = "role_id") // foreign key in join table
    )
    private Set<MasterRole> roles = new HashSet<>();


}
