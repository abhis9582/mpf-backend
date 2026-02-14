package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.OTP;
import com.mypropertyfact.estate.repositories.OTPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class OTPService {

    private final OTPRepository otpRepository;

    // Validate email address
    private void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Please enter a valid email address");
        }
    }

    // Normalize email address
    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.toLowerCase();
    }

    public String generateOTP(String email) {
        validateEmail(email);
        String normalizedEmail = normalizeEmail(email);
        // Generate 6-digit OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        
        // Create OTP entity
        OTP otp = new OTP();
        otp.setEmail(normalizedEmail);
        otp.setOtpCode(otpCode);
        otp.setIsVerified(false);
        otp.setCreatedAt(new Date());
        otp.setExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 minutes
        // Save OTP
        otpRepository.save(otp);
        log.info("OTP generated for email: {}", normalizedEmail);
        return otpCode;
    }
    
    /**
     * Verify OTP code
     */
    public boolean verifyOTP(String email, String otpCode) {
        // Normalize phone number for lookup
        String normalizedEmail = normalizeEmail(email);
        
        Optional<OTP> otp = otpRepository.findByEmailAndOtpCodeAndIsVerified(
            normalizedEmail, otpCode, false);
        
        if (otp.isEmpty()) {
            return false;
        }
        
        OTP otpEntity = otp.get();
        
        // Check if OTP is expired
        if (otpEntity.getExpiresAt().before(new Date())) {
            log.warn("OTP expired for email: {}", email);
            return false;
        }
        
        // Mark as verified
        otpEntity.setIsVerified(true);
        otpRepository.save(otpEntity);
        
        log.info("OTP verified for email: {}", email);
        return true;
    }
    
    /**
     * Check if OTP exists and is valid (not verified and not expired)
     */
    public boolean isValidOTP(String email, String otpCode) {
        // Normalize phone number for lookup
        String normalizedEmail = normalizeEmail(email);
        
        Optional<OTP> otp = otpRepository.findByEmailAndOtpCodeAndIsVerified(
            normalizedEmail, otpCode, false);

        return otp.map(value -> value.getExpiresAt().after(new Date())).orElse(false);

    }
}

