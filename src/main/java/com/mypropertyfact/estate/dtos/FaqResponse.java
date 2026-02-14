package com.mypropertyfact.estate.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaqResponse {
    private String projectName;
    private String question;
    private String answer;
    private int projectId;
    private int id;
}
