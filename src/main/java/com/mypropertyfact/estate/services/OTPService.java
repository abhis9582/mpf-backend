package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.OTP;
import com.mypropertyfact.estate.repositories.OTPRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class OTPService {
    
    @Autowired
    private OTPRepository otpRepository;
    
    /**
     * Normalize and validate phone number - extracts exactly 10 digits
     * Accepts formats like: +91 12345 67890, 1234567890, (123) 456-7890, etc.
     */
    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        // Remove all non-digit characters
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");
        
        // Extract last 10 digits (in case country code is included)
        if (digitsOnly.length() > 10) {
            digitsOnly = digitsOnly.substring(digitsOnly.length() - 10);
        }
        
        return digitsOnly;
    }
    
    /**
     * Validate phone number is exactly 10 digits
     */
    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        
        String normalized = normalizePhoneNumber(phoneNumber);
        
        if (normalized.length() != 10) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        }
        
        // Check if all digits are the same (invalid)
        if (normalized.matches("(\\d)\\1{9}")) {
            throw new IllegalArgumentException("Please enter a valid phone number");
        }
    }
    
    /**
     * Generate and save OTP for phone number
     * For development: returns OTP in response instead of sending SMS
     * For production: integrate with SMS provider (Twilio, AWS SNS, etc.)
     */
    public String generateOTP(String phoneNumber) {
        // Validate phone number format
        validatePhoneNumber(phoneNumber);
        
        // Normalize phone number (extract 10 digits)
        String normalizedPhone = normalizePhoneNumber(phoneNumber);
        
        // Generate 6-digit OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        
        // Create OTP entity
        OTP otp = new OTP();
        otp.setPhoneNumber(normalizedPhone);
        otp.setOtpCode(otpCode);
        otp.setIsVerified(false);
        otp.setCreatedAt(new Date());
        otp.setExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 minutes
        
        // Save OTP
        otpRepository.save(otp);
        
        log.info("OTP generated for phone: {}", normalizedPhone);
        
        // For now, return OTP in response (remove in production)
        return otpCode;
    }
    
    /**
     * Verify OTP code
     */
    public boolean verifyOTP(String phoneNumber, String otpCode) {
        // Normalize phone number for lookup
        String normalizedPhone = normalizePhoneNumber(phoneNumber);
        
        Optional<OTP> otp = otpRepository.findByPhoneNumberAndOtpCodeAndIsVerified(
            normalizedPhone, otpCode, false);
        
        if (otp.isEmpty()) {
            return false;
        }
        
        OTP otpEntity = otp.get();
        
        // Check if OTP is expired
        if (otpEntity.getExpiresAt().before(new Date())) {
            log.warn("OTP expired for phone: {}", phoneNumber);
            return false;
        }
        
        // Mark as verified
        otpEntity.setIsVerified(true);
        otpRepository.save(otpEntity);
        
        log.info("OTP verified for phone: {}", phoneNumber);
        return true;
    }
    
    /**
     * Check if OTP exists and is valid (not verified and not expired)
     */
    public boolean isValidOTP(String phoneNumber, String otpCode) {
        // Normalize phone number for lookup
        String normalizedPhone = normalizePhoneNumber(phoneNumber);
        
        Optional<OTP> otp = otpRepository.findByPhoneNumberAndOtpCodeAndIsVerified(
            normalizedPhone, otpCode, false);
        
        if (otp.isEmpty()) {
            return false;
        }
        
        // Check if expired
        return otp.get().getExpiresAt().after(new Date());
    }
}

