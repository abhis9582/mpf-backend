package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.LoginUserDto;
import com.mypropertyfact.estate.configs.dtos.RegisterUserDto;
import com.mypropertyfact.estate.entities.User;
import com.mypropertyfact.estate.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        // Set role if provided, otherwise it will default to "ROLE_USER"
        if (input.getRole() != null && !input.getRole().isEmpty()) {
            user.setRole(input.getRole());
        }
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public User signupWithoutPassword(RegisterUserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        // Set role if provided, otherwise it will default to "ROLE_USER"
        if (dto.getRole() != null && !dto.getRole().isEmpty()) {
            user.setRole(dto.getRole());
        } else {
            user.setRole("ROLE_USER"); // Default role is USER
        }
        // Generate a secure random password (never used for login)
        String randomPassword = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(randomPassword));
        userRepository.save(user);
        return user;
    }
}
