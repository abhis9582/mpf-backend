package com.mypropertyfact.estate.enums;

public enum Roles {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    AGENT("ROLE_AGENT"),
    BROKER("ROLE_BROKER"),
    OWNER("ROLE_OWNER"),
    TENANT("ROLE_TENANT"),
    LANDLORD("ROLE_LANDLORD"),
    PROPERTY_MANAGER("ROLE_PROPERTY_MANAGER"),
    PROPERTY_OWNER("ROLE_PROPERTY_OWNER"),
    PROPERTY_AGENT("ROLE_PROPERTY_AGENT"),
    PROPERTY_BROKER("ROLE_PROPERTY_BROKER");

    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
