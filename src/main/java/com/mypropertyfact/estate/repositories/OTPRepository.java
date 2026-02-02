package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Integer> {
    
    // Find the most recent unverified OTP for an email
    @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.isVerified = false ORDER BY o.createdAt DESC")
    Optional<OTP> findLatestByEmail(@Param("email") String email);
    
    // Find OTP by phone and code
    Optional<OTP> findByEmailAndOtpCodeAndIsVerified(String email, String otpCode, Boolean isVerified);
    
    // Mark OTP as used/expired
    @Modifying
    @Query("UPDATE OTP o SET o.isVerified = true WHERE o.email = :email AND o.otpCode = :otpCode")
    void markAsVerified(@Param("email") String email, @Param("otpCode") String otpCode);
    
    // Clean up expired OTPs
    @Modifying
    @Query("DELETE FROM OTP o WHERE o.expiresAt < :currentDate")
    void deleteExpiredOTPs(@Param("currentDate") Date currentDate);
}

