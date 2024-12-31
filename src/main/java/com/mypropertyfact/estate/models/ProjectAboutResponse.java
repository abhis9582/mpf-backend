package com.mypropertyfact.estate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectAboutResponse {
    private int id;
    private int projectId;
    private String projectName;
    private String shortDesc;
    private String longDesc;
}
