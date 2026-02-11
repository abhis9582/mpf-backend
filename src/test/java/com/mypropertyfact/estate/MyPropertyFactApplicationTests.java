package com.mypropertyfact.estate;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.mypropertyfact.estate.configs.dtos.LoginUserDto;
import com.mypropertyfact.estate.entities.MasterRole;
import com.mypropertyfact.estate.entities.User;
import com.mypropertyfact.estate.services.AuthenticationService;
import com.mypropertyfact.estate.services.JwtService;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class MyPropertyFactApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private JwtService jwtService;

	private final String jwtToken = "dummy-jwt-token";
	private final String refreshToken = "dummy-refresh-token";

	@Test
	void contextLoads() {
	}

	@Test
	void testLoginSuccess() {
		User mockUser = new User();
		mockUser.setId(1);
		mockUser.setEmail("admin@example.com");
		MasterRole mockRole = new MasterRole();
		mockRole.setRoleName("SUPERADMIN");
		mockRole.setDescription("Super admin role");
		mockRole.setIsActive(true);
		mockUser.setRoles(Set.of(mockRole));

		Mockito.when(
				authenticationService.authenticate(Mockito.any(LoginUserDto.class)))
				.thenReturn(mockUser);
		Mockito.when(jwtService.generateToken(mockUser)).thenReturn(jwtToken);
		Mockito.when(jwtService.generateRefreshToken(mockUser)).thenReturn(refreshToken);
		try {
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"admin\",\"password\":\"pass\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.user.username").value("admin"))
				.andExpect(cookie().exists("token"))
				.andExpect(cookie().exists("refreshToken"))
				.andExpect(cookie().value("token", jwtToken))
				.andExpect(cookie().value("refreshToken", refreshToken));
		}catch (Exception e) {
			log.error("Error in testLoginSuccess", e);
		}
	}

	@Test
    void testLoginFailure_InvalidCredentials() throws Exception {
        // Simulate authentication failure
        Mockito.when(authenticationService.authenticate(Mockito.any(LoginUserDto.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isInternalServerError());
    }

}
