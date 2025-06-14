package com.mypropertyfact.estate.configs.dtos;
import lombok.Data;

@Data
public class ProjectAboutDto {
    private int id;
    private int projectId;
    private String shortDesc;
    private String longDesc;
}
