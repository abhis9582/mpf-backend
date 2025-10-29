package com.mypropertyfact.estate.configs.dtos;

import lombok.Data;

@Data
public class ProjectWalkthroughDto {
    private int id;
    private int projectId;
    private String projectName;
    private String walkthroughDesc;
    private String walkthroughImage;
}
