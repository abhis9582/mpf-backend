package com.mypropertyfact.estate.configs.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterUserDto {
    private String email;

    private String password;

    private String fullName;
}