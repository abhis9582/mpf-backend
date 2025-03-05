package com.mypropertyfact.estate.configs.dtos;

import com.mypropertyfact.estate.entities.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginResponse {
    private String token;

    private long expiresIn;
    private User user;

    public String getToken() {
        return token;
    }
}
