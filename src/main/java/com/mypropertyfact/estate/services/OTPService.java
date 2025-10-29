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
     * Generate and save OTP for phone number
     * For development: returns OTP in response instead of sending SMS
     * For production: integrate with SMS provider (Twilio, AWS SNS, etc.)
     */
    public String generateOTP(String phoneNumber) {
        // Generate 6-digit OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        
        // Create OTP entity
        OTP otp = new OTP();
        otp.setPhoneNumber(phoneNumber);
        otp.setOtpCode(otpCode);
        otp.setIsVerified(false);
        otp.setCreatedAt(new Date());
        otp.setExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 minutes
        
        // Save OTP
        otpRepository.save(otp);
        
        log.info("OTP generated for phone: {}", phoneNumber);
        
        // For now, return OTP in response (remove in production)
        return otpCode;
    }
    
    /**
     * Verify OTP code
     */
    public boolean verifyOTP(String phoneNumber, String otpCode) {
        Optional<OTP> otp = otpRepository.findByPhoneNumberAndOtpCodeAndIsVerified(
            phoneNumber, otpCode, false);
        
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
        Optional<OTP> otp = otpRepository.findByPhoneNumberAndOtpCodeAndIsVerified(
            phoneNumber, otpCode, false);
        
        if (otp.isEmpty()) {
            return false;
        }
        
        // Check if expired
        return otp.get().getExpiresAt().after(new Date());
    }
}

