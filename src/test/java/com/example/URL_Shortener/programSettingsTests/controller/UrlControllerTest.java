package com.example.URL_Shortener.programSettingsTests.controller;

import com.example.URL_Shortener.programSettings.controller.UrlController;
import com.example.URL_Shortener.programSettings.dto.InputOriginalUrlDto;
import com.example.URL_Shortener.programSettings.dto.UrlDto;
import com.example.URL_Shortener.programSettings.entity.Url;
import com.example.URL_Shortener.programSettings.manager.UrlManager;
import com.example.URL_Shortener.programSettings.mapper.UrlMapper;
import com.example.URL_Shortener.programSettings.service.UrlService;
import com.example.URL_Shortener.securitySettings.jwt.JwtAuthenticationFilter;
import com.example.URL_Shortener.securitySettings.security.config.SecurityConfig;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private UrlManager manager;

    @MockitoBean
    private UrlService service;

    @MockitoBean
    private UrlMapper mapper;

    private Authentication authenticatedUser() {
        return new UsernamePasswordAuthenticationToken("Denys", "N/A", List.of());
    }

    @Test
    void shouldCreateShortUrl() throws Exception {
        InputOriginalUrlDto input = new InputOriginalUrlDto();
        input.setUrl("https://google.com");

        UrlDto dto = new UrlDto();
        dto.setOriginalUrl("https://google.com");
        dto.setCreatorName("Denys");

        when(mapper.inputOriginalUrlAndUsernameToUrlDto(any(), eq("Denys")))
                .thenReturn(dto);
        when(manager.manageUrl(dto)).thenReturn("abc123");

        mockMvc.perform(post("/api/v1/urls")
                        .principal(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().string("abc123"));
    }

    @Test
    void shouldReturnAllUrlsForCurrentUser() throws Exception {
        UrlDto dto = new UrlDto(1L, "https://google.com", "abc123", "Denys",
                LocalDate.now(), LocalDate.now().plusDays(20), 0L);

        when(service.findAllUrlByCreatorName("Denys")).thenReturn(List.of(new Url()));
        when(mapper.allUrlToAllDto(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/urls")
                        .principal(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].newUrl").value("abc123"));
    }

    @Test
    void shouldReturnOnlyActiveUrlsForCurrentUser() throws Exception {
        UrlDto dto = new UrlDto(1L, "https://google.com", "abc123", "Denys",
                LocalDate.now(), LocalDate.now().plusDays(20), 0L);

        when(service.findAllActiveUrlByCreatorName("Denys")).thenReturn(List.of(new Url()));
        when(mapper.allUrlToAllDto(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/urls/active")
                        .principal(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].newUrl").value("abc123"));
    }

    @Test
    void shouldRedirectToOriginalUrlWithoutAuthentication() throws Exception {
        when(manager.extractUrl("abc123")).thenReturn("https://google.com");

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

        mockMvc.perform(put("/api/v1/urls")
                        .principal(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }
}