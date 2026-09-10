package com.example.URL_Shortener.programSettings.controller;

import com.example.URL_Shortener.programSettings.dto.InputOriginalUrlDto;
import com.example.URL_Shortener.programSettings.dto.InputShortUrlDto;
import com.example.URL_Shortener.programSettings.dto.UrlDto;
import com.example.URL_Shortener.programSettings.entity.Url;
import com.example.URL_Shortener.programSettings.manager.UrlManager;
import com.example.URL_Shortener.programSettings.mapper.UrlMapper;
import com.example.URL_Shortener.programSettings.service.UrlService;
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
    private final UrlManager manager;
    private final UrlMapper mapper;

    public UrlController(UrlManager manager, UrlService service, UrlMapper mapper) {
        this.manager = manager;
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<String> createShortUrl(Authentication authentication, @Valid @RequestBody InputOriginalUrlDto input){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(manager.manageUrl(mapper.inputOriginalUrlAndUsernameToUrlDto(input, authentication.getName())));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUrl(Authentication authentication, @RequestBody InputShortUrlDto input){
        UrlDto dto = mapper.inputShortlUrlAndUsernameToUrlDto(input, authentication.getName());
        Url url = mapper.urlDtoToUrl(dto);
        service.delete(url);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateUrl(Authentication authentication, @RequestBody InputShortUrlDto input){
        UrlDto dto = mapper.inputShortlUrlAndUsernameToUrlDto(input, authentication.getName());
        Url url = mapper.urlDtoToUrl(dto);
        service.update(url);
    }

    @GetMapping
    public ResponseEntity<List<UrlDto>> getAllUrls(Authentication authentication){
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.allUrlToAllDto(service.findAllUrlByCreatorName(authentication.getName())));
    }

    @GetMapping("/active")
    public ResponseEntity<List<UrlDto>> getActiveUrls(Authentication authentication){
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.allUrlToAllDto(service.findAllActiveUrlByCreatorName(authentication.getName())));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl){
        String originalUrl = manager.extractUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
