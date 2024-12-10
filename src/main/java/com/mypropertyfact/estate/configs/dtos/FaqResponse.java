package com.mypropertyfact.estate.configs.dtos;

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
}
