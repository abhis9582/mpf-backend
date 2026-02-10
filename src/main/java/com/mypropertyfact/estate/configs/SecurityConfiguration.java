package com.mypropertyfact.estate.configs;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,        // Enables @Secured annotation
    jsr250Enabled = true,          // Enables @RolesAllowed annotation
    prePostEnabled = true          // Enables @PreAuthorize, @PostAuthorize annotations
)
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // Updated syntax for disabling CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**")
                        .authenticated()
                        .requestMatchers("/api/admin/**")
                        .hasRole("SUPERADMIN")
                        .requestMatchers("/api/user/**")
                        .authenticated()
                        .requestMatchers("/users/me")
                        .authenticated()
                        .requestMatchers("/api/public/**")
                        .permitAll()
                        .requestMatchers("/auth/session").authenticated()
                        .requestMatchers("/auth/refresh").authenticated()
                        .requestMatchers("/auth/logout").permitAll() 
                        .anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"timestamp\":\"" + java.time.Instant.now() + "\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication required\",\"path\":\"" + request.getRequestURI() + "\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"timestamp\":\"" + java.time.Instant.now() + "\",\"status\":403,\"error\":\"Forbidden\",\"message\":\"Access denied - insufficient permissions\",\"path\":\"" + request.getRequestURI() + "\"}");
                        })
                );
        return http.build();
    }
    
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        // Allow localhost for development
        config.addAllowedOriginPattern("http://localhost:*");
        // Allow production frontend domains
        config.addAllowedOriginPattern("https://mypropertyfact.in");
        config.addAllowedOriginPattern("https://mypropertyfact.com");
        config.addAllowedOriginPattern("http://mypropertyfact.in");
        config.addAllowedOriginPattern("http://mypropertyfact.com");
        config.addAllowedOriginPattern("https://mpf-chatbot2.onrender.com");
        config.addAllowedHeader("*");
        
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
