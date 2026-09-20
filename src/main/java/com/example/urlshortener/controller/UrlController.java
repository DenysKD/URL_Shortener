package com.example.urlshortener.controller;

import com.example.urlshortener.dto.InputOriginalUrlDto;
import com.example.urlshortener.dto.InputShortUrlDto;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/urls")
@SecurityRequirement(name = "bearerAuth")
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(Authentication authentication, @Valid @RequestBody InputOriginalUrlDto input){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createShortUrl(input.getUrl(), authentication.getName()));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUrl(Authentication authentication, @Valid @RequestBody InputShortUrlDto input){
        service.deleteByShortUrl(input.getUrl(), authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<UrlResponse> updateUrl(Authentication authentication, @Valid @RequestBody InputShortUrlDto input){
        return ResponseEntity.accepted()
                .body(service.regenerateShortUrl(input.getUrl(), authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<UrlResponse>> getAllUrls(Authentication authentication){
        return ResponseEntity.ok(service.findAllUrlByCreatorName(authentication.getName()));
    }

    @GetMapping("/active")
    public ResponseEntity<List<UrlResponse>> getActiveUrls(Authentication authentication){
        return ResponseEntity.ok(service.findAllActiveUrlByCreatorName(authentication.getName()));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl){
        String originalUrl = service.resolveOriginalUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
