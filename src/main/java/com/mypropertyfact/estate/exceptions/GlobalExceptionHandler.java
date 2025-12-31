package com.mypropertyfact.estate.exceptions;

import com.mypropertyfact.estate.models.ErrorResponse;
import com.mypropertyfact.estate.models.ResourceNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Handle expired JWT tokens gracefully - don't log as error
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException ex) {
        // Log at debug level only, not as an error
        log.debug("JWT token expired: {}", ex.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("error", "Unauthorized");
        error.put("message", "JWT token has expired. Please refresh your token or login again.");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // Handle authentication failures (bad credentials) gracefully
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        // Log at info level, not as an error (this is expected behavior for wrong credentials)
        log.info("Authentication failed: Invalid credentials provided");
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("error", "Unauthorized");
        error.put("message", "Invalid email or password. Please check your credentials and try again.");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex){
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Handle missing static resources (e.g., favicon.ico) gracefully - don't log as error
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        String resourcePath = ex.getResourcePath();
        
        // Only log at debug level for common browser requests like favicon.ico
        if (resourcePath != null && (resourcePath.equals("/favicon.ico") || 
                                     resourcePath.startsWith("/favicon") ||
                                     resourcePath.endsWith(".ico"))) {
            log.debug("Static resource not found (browser request): {}", resourcePath);
            // Return empty 404 response for favicon requests
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        // Log other missing resources at info level (not error)
        log.info("Static resource not found: {}", resourcePath);
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Resource Not Found");
        error.put("message", "The requested resource was not found.");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    // Handles validation errors (e.g. @NotBlank, @Size)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handles ResponseStatusException thrown from service layer
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getReason());
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    // Handles ConstraintViolationException (entity-level validation during persistence)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        // Extract all constraint violations
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            fieldErrors.put(fieldName, message);
        }
        
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Failed");
        errorResponse.put("message", "Validation errors occurred");
        errorResponse.put("errors", fieldErrors);
        
        // If there's only one error, also include it at the top level for easy access
        if (fieldErrors.size() == 1) {
            String firstError = fieldErrors.values().iterator().next();
            errorResponse.put("message", firstError);
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handles data integrity violations (e.g., duplicate email, unique constraint violations)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String errorMessage = ex.getMessage();
        String userFriendlyMessage = "An error occurred while processing your request.";
        
        // Check for duplicate email error
        if (errorMessage != null) {
            if (errorMessage.contains("Duplicate entry") && errorMessage.contains("email")) {
                // Extract email from error message if possible
                userFriendlyMessage = "An account with this email address already exists. Please use a different email or try logging in instead.";
            } else if (errorMessage.contains("Duplicate entry") && errorMessage.contains("phone")) {
                userFriendlyMessage = "An account with this phone number already exists. Please use a different phone number or try logging in instead.";
            } else if (errorMessage.contains("Duplicate entry")) {
                userFriendlyMessage = "This information is already in use. Please use different details.";
            }
        }
        
        log.info("Data integrity violation: {}", errorMessage);
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Conflict");
        error.put("message", userFriendlyMessage);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    //Handles IllegalArgumentException in whole project
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Validation Error");
        error.put("message", ex.getMessage() != null ? ex.getMessage() : "Invalid input provided. Please check your data and try again.");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles RuntimeException (wrapped exceptions from service layer)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String userMessage = ex.getMessage();
        
        // Extract user-friendly message if wrapped exception exists
        if (ex.getCause() != null && ex.getCause() instanceof IllegalArgumentException) {
            userMessage = ex.getCause().getMessage();
        } else if (userMessage == null || userMessage.isEmpty()) {
            userMessage = "An unexpected error occurred. Please try again later.";
        }
        
        // Check if it's a known error or generic error
        if (ex.getMessage() != null && ex.getMessage().contains("database") || 
            (ex.getCause() != null && ex.getCause().getMessage() != null && 
             ex.getCause().getMessage().contains("database"))) {
            userMessage = "A database error occurred. Please try again later or contact support if the problem persists.";
        }
        
        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Error");
        error.put("message", userMessage);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Optional: Handles other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred. Please try again later or contact support if the problem persists.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
