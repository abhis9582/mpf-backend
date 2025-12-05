package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${security.jwt.refresh.expiration-time}")
    private long jwtRefreshTokenExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user, jwtRefreshTokenExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, User user, long expiration) {
        // Add role and permissions to JWT claims
        // Extract role names from MasterRole entities
        Set<String> userRoles = user.getRoles() != null 
            ? user.getRoles().stream()
                .filter(role -> role != null && role.getIsActive() != null && role.getIsActive())
                .map(role -> "ROLE_" + role.getRoleName())
                .collect(Collectors.toSet())
            : Set.of("ROLE_USER"); // Default role if no roles assigned
        extraClaims.put("role", userRoles);
        
        // Extract permissions from user's authorities (roles)
        // List<String> permissions = user.getAuthorities().stream()
        //         .map(authority -> authority.getAuthority())
        //         .toList();
        // extraClaims.put("permissions", permissions);
        extraClaims.put("email", user.getEmail());
        extraClaims.put("userId", user.getId());
        extraClaims.put("fullName", user.getFullName());
        
        return buildToken(extraClaims, user, expiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            User user,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extract roles from JWT token
     */
    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roleClaim = claims.get("role");
        if (roleClaim instanceof Set) {
            return (Set<String>) roleClaim;
        } else if (roleClaim instanceof List) {
            return new HashSet<>((List<String>) roleClaim);
        } else if (roleClaim instanceof String) {
            return Set.of((String) roleClaim);
        }
        return Set.of("ROLE_USER"); // Default role
    }

    /**
     * Extract permissions from JWT token
     */
    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("permissions", List.class);
    }

    /**
     * Extract user ID from JWT token
     */
    public Integer extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }

    /**
     * Extract full name from JWT token
     */
    public String extractFullName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("fullName", String.class);
    }
}
