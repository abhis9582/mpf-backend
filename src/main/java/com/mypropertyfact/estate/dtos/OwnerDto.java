package com.mypropertyfact.estate.dtos;

import lombok.Data;

import java.util.List;

@Data
public class OwnerDto {

    private int ownerId;
    private String ownerFullName;
    private String ownerMobileNumber;
    private String ownerEmail;
    private Boolean isAcceptedTermAndCondition;
    private String entityLegalName;
    private String authorizedSignatoryNameAndEmail;
    private String gstInNumber;
    private Boolean isActive;

    // Reference by IDs instead of full objects
    private Integer ownerTypeId;
    private Integer ownerCityId;
    private List<Integer> roleIds;
    private List<Integer> ownerRegisteredAddressIds;
}
