package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.User;
import com.mypropertyfact.estate.services.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.mypropertyfact.estate.repositories.UserRepository;

import java.util.List;

@RequestMapping("/users")
@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Getting authenticated user");
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }

        // Cast to User since User implements UserDetails
        User currentUser = (User) authentication.getPrincipal();
        
        // Fetch fresh data from database to ensure all fields are present
        User dbUser = userRepository.findById(currentUser.getId()).orElse(currentUser);

        return ResponseEntity.ok(dbUser);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(@RequestBody User updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Updating user profile");
        
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();
        User dbUser = userRepository.findById(currentUser.getId()).orElseThrow();
        
        // Update only allowed fields (don't update email, password, or role via this endpoint)
        if (updatedUser.getFullName() != null) {
            dbUser.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getPhone() != null) {
            dbUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getLocation() != null) {
            dbUser.setLocation(updatedUser.getLocation());
        }
        if (updatedUser.getBio() != null) {
            dbUser.setBio(updatedUser.getBio());
        }
        if (updatedUser.getAvatar() != null) {
            dbUser.setAvatar(updatedUser.getAvatar());
        }
        if (updatedUser.getExperience() != null) {
            dbUser.setExperience(updatedUser.getExperience());
        }
        if (updatedUser.getRating() != null) {
            dbUser.setRating(updatedUser.getRating());
        }
        if (updatedUser.getTotalDeals() != null) {
            dbUser.setTotalDeals(updatedUser.getTotalDeals());
        }
        if (updatedUser.getVerified() != null) {
            dbUser.setVerified(updatedUser.getVerified());
        }
        
        User savedUser = userRepository.save(dbUser);
        
        log.info("User profile updated successfully for user: {}", savedUser.getEmail());
        
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }
}
