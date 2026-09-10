package com.example.URL_Shortener.securitySettingsTests.service;

import com.example.URL_Shortener.securitySettings.dto.AuthenticationRequest;
import com.example.URL_Shortener.securitySettings.dto.AuthenticationResponse;
import com.example.URL_Shortener.securitySettings.dto.RegisterRequest;
import com.example.URL_Shortener.securitySettings.entity.Role;
import com.example.URL_Shortener.securitySettings.entity.User;
import com.example.URL_Shortener.securitySettings.jwt.JwtService;
import com.example.URL_Shortener.securitySettings.security.service.AuthenticationService;
import com.example.URL_Shortener.securitySettings.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void shouldRegisterNewUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest("Denys", "Password123");

        when(userService.save(any(User.class))).thenReturn(new User("Denys", "encoded", Role.USER));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.register(request);

        assertEquals("jwt-token", response.getToken());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        assertEquals("Denys", captor.getValue().getUsername());
    }

    @Test
    void shouldAuthenticateExistingUserAndReturnToken() {
        AuthenticationRequest request = new AuthenticationRequest("Denys", "Password123");
        User user = new User("Denys", "encoded", Role.USER);

        when(userService.loadUserByUsername("Denys")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
