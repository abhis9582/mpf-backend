package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.LoginUserDto;
import com.mypropertyfact.estate.configs.dtos.RegisterUserDto;
import com.mypropertyfact.estate.entities.MasterRole;
import com.mypropertyfact.estate.entities.User;
import com.mypropertyfact.estate.models.ResourceNotFoundException;
import com.mypropertyfact.estate.repositories.MasterRoleRepository;
import com.mypropertyfact.estate.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final MasterRoleRepository masterRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            MasterRoleRepository masterRoleRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.masterRoleRepository = masterRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signup(RegisterUserDto input) {
        // Check if user with this email already exists
        if (input.getEmail() != null && userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new IllegalArgumentException("An account with this email address already exists. Please use a different email or try logging in instead.");
        }
        
        User user = new User();
        // Ensure fullName is not null - use email or default value if not provided
        String fullName = (input.getFullName() != null && !input.getFullName().trim().isEmpty()) 
                ? input.getFullName().trim() 
                : (input.getEmail() != null ? input.getEmail().split("@")[0] : "User");
        user.setFullName(fullName);
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        
        // Set default USER role
        Set<MasterRole> roles = new HashSet<>();
        masterRoleRepository.findByRoleNameIgnoreCase("USER")
                .ifPresentOrElse(
                        roles::add,
                        () -> {
                            // Create USER role if it doesn't exist
                            MasterRole userRole = new MasterRole();
                            userRole.setRoleName("USER");
                            userRole.setDescription("Default user role");
                            userRole.setIsActive(true);
                            roles.add(masterRoleRepository.save(userRole));
                        }
                );
        user.setRoles(roles);
        
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        // Authenticate user credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        // If authentication succeeds, retrieve the user from database
        // This should always succeed if authentication passed, but handle edge case gracefully
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User account not found. Please contact support if this issue persists."
                ));
    }

    @Transactional
    public User signupWithoutPassword(RegisterUserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        // Ensure fullName is not null - use email or default value if not provided
        String fullName = (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) 
                ? dto.getFullName().trim() 
                : (dto.getEmail() != null ? dto.getEmail().split("@")[0] : "User");
        user.setFullName(fullName);
        
        // Set default USER role
        Set<MasterRole> roles = new HashSet<>();
        String roleName = (dto.getRole() != null && !dto.getRole().isEmpty()) 
                ? dto.getRole().replace("ROLE_", "") 
                : "USER";
        
        masterRoleRepository.findByRoleNameIgnoreCase(roleName)
                .ifPresentOrElse(
                        roles::add,
                        () -> {
                            // Create role if it doesn't exist
                            MasterRole role = new MasterRole();
                            role.setRoleName(roleName);
                            role.setDescription("User role");
                            role.setIsActive(true);
                            roles.add(masterRoleRepository.save(role));
                        }
                );
        user.setRoles(roles);
        
        // Generate a secure random password (never used for login)
        String randomPassword = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(randomPassword));
        userRepository.save(user);
        return user;
    }
}
