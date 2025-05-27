package com.mypropertyfact.estate.projections;

public interface ProjectView {
    Integer getId();
    String getProjectName();
    String getProjectPrice();
    String getProjectAddress();
    String getProjectThumbnail();
    String getSlugURL();
    int getPropertyType();
}
