package com.mypropertyfact.estate.enums;

public enum ProjectApprovalStatus {
    DRAFT,              // User saved but not yet submitted
    PENDING,            // Submitted and waiting for admin approval
    APPROVED,           // Approved by admin
    REJECTED,           // Rejected by admin
    REQUIRES_CHANGES    // Admin requested changes
}


