package com.mypropertyfact.estate.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MasterBrokerDTO {
    private int brokerId;

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name cannot exceed 150 characters")
    private String brokerFullName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String brokerMobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String brokerEmail;

    @Size(max = 50, message = "RERA registration number cannot exceed 50 characters")
    private String reraAgentRegistrationNumber;

    @Size(max = 10, message = "PAN number must be 10 characters")
    private String panNumber;

    @Size(max = 150, message = "Company association cannot exceed 150 characters")
    private String companyAssociation;

    private Boolean isWhatsAppEnabled = false;

    private Boolean isTermAndConditionAccepted = false;

    private Boolean isActive = true;

    @Size(max = 200, message = "Legal entity name cannot exceed 200 characters")
    private String legalEntityName;

    @Pattern(regexp = "^[0-9A-Z]{15}$", message = "Invalid GSTIN format")
    private String gstInNumber;

    @Size(max = 25, message = "CIN/LLPIN cannot exceed 25 characters")
    private String cinOrLLpin;

    @Size(max = 250, message = "Authorized signatory name/email cannot exceed 250 characters")
    private String authorizedSignatoryNameAndEmail;

    @Size(max = 15, message = "Support number cannot exceed 15 digits")
    private String supportNumber;

    private Boolean isAcceptedTermAndCondition = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // IDs of relations (instead of objects)
    private Integer profileTypeId;
    private Integer reraStateId;
    private Integer brokerCityId;
    private Integer firmTypeId;
    private Integer ownerTypeId;

    private List<Integer> brokerAddressIds;
    private List<Integer> firmRegisteredAddressIds;
    private List<Integer> roleIds;
}
