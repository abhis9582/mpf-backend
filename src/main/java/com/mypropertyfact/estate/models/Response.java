package com.mypropertyfact.estate.models;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private int isSuccess;
    private String message;
}
