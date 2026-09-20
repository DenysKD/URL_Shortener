package com.example.urlshortener.mapper;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.Role;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlMapperTest {

    private final UrlMapper mapper = new UrlMapper();
    private final User creator = new User("Denys", "encoded", Role.USER);

    @Test
    void shouldMapUrlToResponse() {
        Url url = new Url(1L, "https://google.com", "abc123", creator,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 21), 5L);

        UrlResponse result = mapper.urlToResponse(url);

        assertEquals(1L, result.getId());
        assertEquals("https://google.com", result.getOriginalUrl());
        assertEquals("abc123", result.getNewUrl());
        assertEquals("Denys", result.getCreatorName());
        assertEquals(LocalDate.of(2026, 1, 1), result.getCreatedAt());
        assertEquals(LocalDate.of(2026, 1, 21), result.getExpiredIn());
        assertEquals(5L, result.getTransitionCount());
    }

    @Test
    void shouldMapCollectionOfUrlsToResponseList() {
        Url first = new Url("https://google.com", "abc123", creator);
        Url second = new Url("https://example.com", "def456", creator);

        List<UrlResponse> result = mapper.allUrlToAllResponse(List.of(first, second));

        assertEquals(2, result.size());
    }
}
