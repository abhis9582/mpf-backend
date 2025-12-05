package com.mypropertyfact.estate.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.mypropertyfact.estate.configs.dtos.LoginResponse;
import com.mypropertyfact.estate.configs.dtos.LoginUserDto;
import com.mypropertyfact.estate.configs.dtos.RegisterUserDto;
import com.mypropertyfact.estate.dtos.TokenRequest;
import com.mypropertyfact.estate.entities.MasterRole;
import com.mypropertyfact.estate.entities.User;
import com.mypropertyfact.estate.repositories.MasterRoleRepository;
import com.mypropertyfact.estate.repositories.UserRepository;
import com.mypropertyfact.estate.services.AuthenticationService;
import com.mypropertyfact.estate.services.JwtService;
import com.mypropertyfact.estate.services.OTPService;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private static final String CLIENT_ID = "699880944933-smqbrqmo1lffls5sj7lq77hmhmgqnv19.apps.googleusercontent.com";
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final MasterRoleRepository masterRoleRepository;
    private final OTPService otpService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserRepository userRepository,
                                    MasterRoleRepository masterRoleRepository, OTPService otpService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.masterRoleRepository = masterRoleRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setUser(authenticatedUser);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/google")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody TokenRequest tokenRequest) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance()
        ).setAudience(Collections.singletonList(CLIENT_ID)).build();

        GoogleIdToken idToken = verifier.verify(tokenRequest.getToken());

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            User user;
            String userStatus;
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                // User already registered
                user = existingUser.get();
                userStatus = "old";
            } else {
                // Register new user
                RegisterUserDto registerUserDto = new RegisterUserDto();
                registerUserDto.setEmail(email);
                // Ensure fullName is not null - use email prefix or default if name is not available
                String userFullName = (name != null && !name.trim().isEmpty()) 
                        ? name.trim() 
                        : (email != null ? email.split("@")[0] : "User");
                registerUserDto.setFullName(userFullName);
                registerUserDto.setPassword(UUID.randomUUID().toString()); // random password since using Google login
                user = authenticationService.signupWithoutPassword(registerUserDto);
                userStatus = "new";
            }
            String jwtToken = jwtService.generateToken(user);
            Map<String, Object> data = new HashMap<>();
            data.put("status", userStatus);
            data.put("token", jwtToken);
            data.put("email", user.getEmail());
            data.put("fullName", user.getFullName());
            return ResponseEntity.ok(data);
        } else {
            throw new RuntimeException("Invalid Google token");
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7); // remove "Bearer "

            // Verify token
            Claims claims = jwtService.validateToken(token); // custom util (explained below)

            // Extract roles from token
            Set<String> roles = jwtService.extractRoles(token);
            List<String> rolesList = new ArrayList<>(roles);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("email", claims.getSubject());
            response.put("expiresAt", claims.getExpiration().toString());
            response.put("roles", rolesList);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        try {
            Claims claims = jwtService.validateToken(refreshToken);
            String username = claims.getSubject();

            Optional<User> userDetails = userRepository.findByEmail(username);
            LoginResponse loginResponse = new LoginResponse();
            userDetails.ifPresent(user -> {
                // Generate new access + refresh tokens
                String jwtToken = jwtService.generateToken(user);
                String refToken = jwtService.generateRefreshToken(user);
                loginResponse.setToken(jwtToken);
                loginResponse.setRefreshToken(refToken);
                loginResponse.setExpiresIn(jwtService.getExpirationTime());
                loginResponse.setUser(user);
            });
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    /**
     * Send OTP to mobile number
     * POST /auth/send-otp
     * Body: { "phoneNumber": "+911234567890" }
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOTP(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Phone number is required", "message", "Please enter your phone number"));
            }

            // Generate and send OTP (validation happens inside OTPService)
            String otpCode = otpService.generateOTP(phoneNumber);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "OTP sent successfully",
                "otp", otpCode, // In production, remove this - return only in development
                "expiresIn", 300 // 5 minutes
            ));
            
        } catch (IllegalArgumentException e) {
            // User-friendly validation errors
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage(), "message", e.getMessage()));
        } catch (Exception e) {
            // Check for database/data truncation errors
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Data truncation")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid phone number format", 
                                "message", "Please enter a valid 10-digit phone number"));
            }
            // Generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to send OTP", 
                            "message", "Unable to send OTP. Please check your phone number and try again."));
        }
    }

    /**
     * Verify OTP and register/login user
     * POST /auth/verify-otp
     * Body: { "phoneNumber": "+911234567890", "otp": "123456", "fullName": "John Doe" }
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTPAndRegister(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String otpCode = request.get("otp");
            String fullName = request.get("fullName");

            if (phoneNumber == null || phoneNumber.isEmpty() || 
                otpCode == null || otpCode.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Phone number and OTP are required",
                                "message", "Please enter both phone number and OTP"));
            }

            // Verify OTP (phone number will be normalized inside verifyOTP)
            boolean isValid = otpService.verifyOTP(phoneNumber, otpCode);
            
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid or expired OTP",
                                "message", "The OTP you entered is incorrect or has expired. Please request a new OTP."));
            }

            // Check if user exists
            Optional<User> existingUser = userRepository.findByPhone(phoneNumber);
            User user;
            String userStatus;

            if (existingUser.isPresent()) {
                // User exists - login
                user = existingUser.get();
                userStatus = "existing";
            } else {
                // New user - register
                user = new User();
                // Ensure id is null so it can be auto-generated
                user.setId(null);
                user.setPhone(phoneNumber);
                // Ensure fullName is not null or empty
                String userFullName = (fullName != null && !fullName.trim().isEmpty()) 
                        ? fullName.trim() 
                        : "User";
                user.setFullName(userFullName);
                // Generate a random email if not provided
                user.setEmail(phoneNumber + "@mobile.user");
                // Generate a secure random password
                String randomPassword = UUID.randomUUID().toString();
                user.setPassword(passwordEncoder.encode(randomPassword));
                
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
                user.setVerified(true); // Verified via OTP
                
                user = userRepository.save(user);
                userStatus = "new";
            }

            // Generate JWT token
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtToken);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());

            // Return response
            Map<String, Object> response = new HashMap<>();
            response.put("status", userStatus);
            response.put("token", jwtToken);
            response.put("refreshToken", refreshToken);
            response.put("expiresIn", jwtService.getExpirationTime());
            // Get role names from MasterRole entities
            List<String> roleNames = user.getRoles() != null 
                ? user.getRoles().stream()
                    .filter(role -> role != null && role.getIsActive() != null && role.getIsActive())
                    .map(role -> "ROLE_" + role.getRoleName())
                    .toList()
                : List.of("ROLE_USER");
            
            response.put("user", Map.of(
                "id", user.getId(),
                "fullName", user.getFullName(),
                "phone", user.getPhone(),
                "email", user.getEmail(),
                "role", roleNames.isEmpty() ? "ROLE_USER" : roleNames.get(0),
                "roles", roleNames,
                "verified", user.getVerified()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Get the root cause of the exception
            Throwable rootCause = e;
            while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
                rootCause = rootCause.getCause();
            }
            
            String errorMessage = rootCause.getMessage();
            String userFriendlyMessage;
            
            // Check for specific database errors and provide user-friendly messages
            if (errorMessage != null) {
                if (errorMessage.contains("Field 'id' doesn't have a default value") || 
                    errorMessage.contains("doesn't have a default value")) {
                    userFriendlyMessage = "Unable to create your account. Please contact support or try again later.";
                } else if (errorMessage.contains("Data truncation")) {
                    userFriendlyMessage = "Invalid data provided. Please check your information and try again.";
                } else if (errorMessage.contains("Duplicate entry") || errorMessage.contains("already exists")) {
                    userFriendlyMessage = "An account with this phone number already exists. Please sign in instead.";
                } else if (errorMessage.contains("ConstraintViolationException") || 
                          errorMessage.contains("constraint")) {
                    userFriendlyMessage = "Invalid information provided. Please check your details and try again.";
                } else {
                    // Generic user-friendly message for other errors
                    userFriendlyMessage = "Unable to complete your registration. Please try again or contact support if the problem persists.";
                }
            } else {
                userFriendlyMessage = "Unable to complete your registration. Please try again or contact support if the problem persists.";
            }
            
            // Log the actual error for debugging (but don't expose it to users)
            // Note: This is a database configuration issue - the users table id column needs AUTO_INCREMENT
            System.err.println("Error verifying OTP: " + errorMessage);
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Registration failed", 
                            "message", userFriendlyMessage));
        }
    }
}
