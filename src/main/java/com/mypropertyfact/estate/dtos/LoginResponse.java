package com.mypropertyfact.estate.dtos;

import com.mypropertyfact.estate.entities.User;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String refreshToken;
    private long expiresIn;
    private User user;
}
