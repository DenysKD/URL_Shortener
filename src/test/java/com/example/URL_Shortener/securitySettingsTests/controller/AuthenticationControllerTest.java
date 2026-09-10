package com.example.URL_Shortener.securitySettingsTests.controller;

import com.example.URL_Shortener.securitySettings.controller.AuthenticationController;
import com.example.URL_Shortener.securitySettings.dto.AuthenticationRequest;
import com.example.URL_Shortener.securitySettings.dto.AuthenticationResponse;
import com.example.URL_Shortener.securitySettings.dto.RegisterRequest;
import com.example.URL_Shortener.securitySettings.security.service.AuthenticationService;
import com.example.URL_Shortener.securitySettings.jwt.JwtAuthenticationFilter;
import com.example.URL_Shortener.securitySettings.security.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthenticationController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationService service;

    @Test
    void shouldRegisterUserAndReturnToken() throws Exception {
        RegisterRequest request = new RegisterRequest("Denys", "Password123");

        when(service.register(org.mockito.ArgumentMatchers.any(RegisterRequest.class)))
                .thenReturn(new AuthenticationResponse("jwt-token"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void shouldRejectRegistrationWithWeakPassword() throws Exception {
        RegisterRequest request = new RegisterRequest("Denys", "weak");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAuthenticateUserAndReturnToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("Denys", "Password123");

        when(service.authenticate(org.mockito.ArgumentMatchers.any(AuthenticationRequest.class)))
                .thenReturn(new AuthenticationResponse("jwt-token"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
}