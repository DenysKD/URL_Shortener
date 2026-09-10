package com.example.URL_Shortener.securitySettings.controller;

import com.example.URL_Shortener.securitySettings.dto.AuthenticationRequest;
import com.example.URL_Shortener.securitySettings.dto.AuthenticationResponse;
import com.example.URL_Shortener.securitySettings.dto.RegisterRequest;
import com.example.URL_Shortener.securitySettings.security.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(service.authenticate(request));
    }
}
