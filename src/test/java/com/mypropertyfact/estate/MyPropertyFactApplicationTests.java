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
	void testLoginSuccess() throws Exception {
		User mockUser = new User();
		mockUser.setId(1);
		mockUser.setEmail("mpf@gmail.com");
		mockUser.setFullName("MPF User");
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

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"mpf@gmail.com\",\"password\":\"mpf@2025\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.user.email").value("mpf@gmail.com"))
				.andExpect(jsonPath("$.user.id").value(1))
				.andExpect(cookie().exists("token"))
				.andExpect(cookie().exists("refreshToken"))
				.andExpect(cookie().value("token", jwtToken))
				.andExpect(cookie().value("refreshToken", refreshToken));
	}

	@Test
	void testLoginFailure_InvalidCredentials() throws Exception {
		Mockito.when(authenticationService.authenticate(Mockito.any(LoginUserDto.class)))
				.thenThrow(new RuntimeException("Invalid credentials"));

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"admin@example.com\",\"password\":\"wrongpass\"}"))
				.andExpect(status().isInternalServerError());
	}

	// --- auth/logout tests ---

	@Test
	void testLogoutSuccess() throws Exception {
		mockMvc.perform(post("/auth/logout"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Logged out"));
	}

	@Test
	void testLogoutClearsCookies() throws Exception {
		mockMvc.perform(post("/auth/logout"))
				.andExpect(status().isOk())
				.andExpect(header().exists("Set-Cookie"))
				.andExpect(jsonPath("$.message").value("Logged out"));
	}

}
