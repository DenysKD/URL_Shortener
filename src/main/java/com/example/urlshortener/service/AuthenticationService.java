package com.example.urlshortener.service;

import com.example.urlshortener.dto.AuthenticationRequest;
import com.example.urlshortener.dto.AuthenticationResponse;
import com.example.urlshortener.dto.RegisterRequest;
import com.example.urlshortener.entity.Role;
import com.example.urlshortener.entity.User;
import com.example.urlshortener.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(AuthenticationManager authenticationManager, UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse register(RegisterRequest request){
        User newUser = new User(request.getUsername(), request.getPassword(), Role.USER);

        userService.save(newUser);

        String token = jwtService.generateToken(newUser);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
        );

        User user = userService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }
}
