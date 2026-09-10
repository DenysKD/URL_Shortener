package com.example.URL_Shortener.programSettingsTests.mapper;

import com.example.URL_Shortener.programSettings.dto.InputOriginalUrlDto;
import com.example.URL_Shortener.programSettings.dto.InputShortUrlDto;
import com.example.URL_Shortener.programSettings.dto.UrlDto;
import com.example.URL_Shortener.programSettings.entity.Url;
import com.example.URL_Shortener.programSettings.mapper.UrlMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlMapperTest {

    private final UrlMapper mapper = new UrlMapper();

    @Test
    void shouldMapInputOriginalUrlAndUsernameToDto() {
        InputOriginalUrlDto input = new InputOriginalUrlDto();
        input.setUrl("https://google.com");

        UrlDto result = mapper.inputOriginalUrlAndUsernameToUrlDto(input, "Denys");

        assertEquals("https://google.com", result.getOriginalUrl());
        assertEquals("Denys", result.getCreatorName());
    }

    @Test
    void shouldMapInputShortUrlAndUsernameToDto() {
        InputShortUrlDto input = new InputShortUrlDto();
        input.setUrl("abc123");

        UrlDto result = mapper.inputShortlUrlAndUsernameToUrlDto(input, "Denys");

        assertEquals("abc123", result.getNewUrl());
        assertEquals("Denys", result.getCreatorName());
    }

    @Test
    void shouldMapStringAndUsernameToDto() {
        UrlDto result = mapper.inputStringAndUsernameToUrlDto("abc123", "Denys");

        assertEquals("abc123", result.getNewUrl());
        assertEquals("Denys", result.getCreatorName());
    }

    @Test
    void shouldDefaultTransitionCountToZeroWhenMissing() {
        UrlDto dto = new UrlDto();
        dto.setOriginalUrl("https://google.com");
        dto.setNewUrl("abc123");
        dto.setCreatorName("Denys");

        Url result = mapper.urlDtoToUrl(dto);

        assertEquals(0L, result.getTransitionCount());
    }

    @Test
    void shouldKeepExistingTransitionCountWhenPresent() {
        UrlDto dto = new UrlDto();
        dto.setOriginalUrl("https://google.com");
        dto.setNewUrl("abc123");
        dto.setCreatorName("Denys");
        dto.setTransitionCount(42L);

        Url result = mapper.urlDtoToUrl(dto);

        assertEquals(42L, result.getTransitionCount());
    }

    @Test
    void shouldMapUrlToDto() {
        Url url = new Url(1L, "https://google.com", "abc123", "Denys",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 21), 5L);

        UrlDto result = mapper.urlToDto(url);

        assertEquals(1L, result.getId());
        assertEquals("https://google.com", result.getOriginalUrl());
        assertEquals("abc123", result.getNewUrl());
        assertEquals("Denys", result.getCreatorName());
        assertEquals(LocalDate.of(2026, 1, 1), result.getCreatedAt());
        assertEquals(LocalDate.of(2026, 1, 21), result.getExpiredIn());
        assertEquals(5L, result.getTransitionCount());
    }

    @Test
    void shouldMapCollectionOfUrlsToDtoList() {
        Url first = new Url("https://google.com", "abc123", "Denys");
        Url second = new Url("https://example.com", "def456", "Denys");

        List<UrlDto> result = mapper.allUrlToAllDto(List.of(first, second));

        assertEquals(2, result.size());
    }
}
