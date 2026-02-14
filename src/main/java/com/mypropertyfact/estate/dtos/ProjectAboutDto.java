package com.mypropertyfact.estate.dtos;
import lombok.Data;

@Data
public class ProjectAboutDto {
    private int id;
    private int projectId;
    private String shortDesc;
    private String longDesc;
}
