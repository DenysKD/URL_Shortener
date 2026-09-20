package com.example.urlshortener.controller;

import com.example.urlshortener.dto.InputOriginalUrlDto;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.security.JwtAuthenticationFilter;
import com.example.urlshortener.security.SecurityConfig;
import com.example.urlshortener.service.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UrlController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
)
@AutoConfigureMockMvc(addFilters = false)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UrlService service;

    private Authentication authenticatedUser() {
        return new UsernamePasswordAuthenticationToken("Denys", "N/A", List.of());
    }

    @Test
    void shouldCreateShortUrl() throws Exception {
        InputOriginalUrlDto input = new InputOriginalUrlDto();
        input.setUrl("https://google.com");

        UrlResponse response = new UrlResponse(1L, "https://google.com", "abc123", "Denys",
                LocalDate.now(), LocalDate.now().plusDays(20), 0L);

        when(service.createShortUrl("https://google.com", "Denys")).thenReturn(response);

        mockMvc.perform(post("/api/v1/urls")
                        .principal(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.newUrl").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://google.com"));
    }

    @Test
    void shouldReturnAllUrlsForCurrentUser() throws Exception {
        UrlResponse dto = new UrlResponse(1L, "https://google.com", "abc123", "Denys",
                LocalDate.now(), LocalDate.now().plusDays(20), 0L);

        when(service.findAllUrlByCreatorName("Denys")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/urls")
                        .principal(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].newUrl").value("abc123"));
    }

    @Test
    void shouldReturnOnlyActiveUrlsForCurrentUser() throws Exception {
        UrlResponse dto = new UrlResponse(1L, "https://google.com", "abc123", "Denys",
                LocalDate.now(), LocalDate.now().plusDays(20), 0L);

        when(service.findAllActiveUrlByCreatorName("Denys")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/urls/active")
                        .principal(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].newUrl").value("abc123"));
    }

    @Test
    void shouldRedirectToOriginalUrlWithoutAuthentication() throws Exception {
        when(service.resolveOriginalUrl("abc123")).thenReturn("https://google.com");

        mockMvc.perform(get("/api/v1/urls/{shortUrl}", "abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://google.com"));
    }

    @Test
    void shouldDeleteUrl() throws Exception {
        String body = "{\"url\":\"abc123\"}";

        mockMvc.perform(delete("/api/v1/urls")
                        .principal(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdateUrl() throws Exception {
        String body = "{\"url\":\"abc123\"}";
        UrlResponse response = new UrlResponse(1L, "https://google.com", "newurl99", "Denys",
                LocalDate.now(), LocalDate.now().plusDays(20), 0L);

        when(service.regenerateShortUrl("abc123", "Denys")).thenReturn(response);

        mockMvc.perform(put("/api/v1/urls")
                        .principal(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.newUrl").value("newurl99"));
    }
}
