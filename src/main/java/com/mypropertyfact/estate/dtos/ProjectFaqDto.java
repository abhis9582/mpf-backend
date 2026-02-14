package com.mypropertyfact.estate.dtos;

import lombok.Data;

@Data
public class ProjectFaqDto {
    private int id;
    private int projectId;
    private String question;
    private String answer;
}
