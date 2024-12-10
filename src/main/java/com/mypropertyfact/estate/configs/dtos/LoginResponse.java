package com.mypropertyfact.estate.configs.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginResponse {
    private String token;

    private long expiresIn;

    public String getToken() {
        return token;
    }
}
