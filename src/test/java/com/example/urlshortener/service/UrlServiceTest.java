package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.Role;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.entity.User;
import com.example.urlshortener.exception.BlankArgumentException;
import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.exception.ShortUrlGenerationException;
import com.example.urlshortener.exception.UrlDoesNotExistException;
import com.example.urlshortener.exception.UrlExpiredException;
import com.example.urlshortener.mapper.UrlMapper;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.util.ShortUrlGenerator;
import com.example.urlshortener.util.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
//-
import org.springframework.dao.DataIntegrityViolationException;
//-

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository repository;

    @Mock
    private ShortUrlGenerator generator;

    @Mock
    private UrlValidator validator;

    @Mock
    private UserService userService;

    private UrlService service;

    private final User creator = new User("Denys", "encoded", Role.USER);

    @BeforeEach
    void setUp() {
        service = new UrlService(repository, generator, validator, new UrlMapper("http://localhost:8080"), userService);
    }

    @Test
    void shouldCreateShortUrl() {
        when(validator.isUrlValid("https://google.com")).thenReturn(true);
        when(userService.loadUserByUsername("Denys")).thenReturn(creator);
        when(generator.generateUrl()).thenReturn("abc123");
        when(repository.existsByNewUrl("abc123")).thenReturn(false);
        when(repository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlResponse result = service.createShortUrl("https://google.com", "Denys");

        assertEquals("http://localhost:8080/api/v1/urls/abc123", result.getNewUrl());
        assertEquals("https://google.com", result.getOriginalUrl());
        assertEquals("Denys", result.getCreatorName());
    }

    @Test
    void shouldRetryCreateWhenSaveViolatesUniqueConstraint() {
        when(validator.isUrlValid("https://google.com")).thenReturn(true);
        when(userService.loadUserByUsername("Denys")).thenReturn(creator);
        when(generator.generateUrl()).thenReturn("first111", "second00");
        when(repository.existsByNewUrl(anyString())).thenReturn(false);
        when(repository.save(any(Url.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UrlResponse result = service.createShortUrl("https://google.com", "Denys");

        assertEquals("http://localhost:8080/api/v1/urls/second00", result.getNewUrl());
        verify(repository, times(2)).save(any(Url.class));
    }

    @Test
    void shouldThrowWhenSaveKeepsViolatingUniqueConstraint() {
        when(validator.isUrlValid("https://google.com")).thenReturn(true);
        when(userService.loadUserByUsername("Denys")).thenReturn(creator);
        when(generator.generateUrl()).thenReturn("taken123");
        when(repository.existsByNewUrl(anyString())).thenReturn(false);
        when(repository.save(any(Url.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(ShortUrlGenerationException.class,
                () -> service.createShortUrl("https://google.com", "Denys"));
        verify(repository, times(5)).save(any(Url.class));
    }

    @Test
    void shouldRegenerateShortUrlOnCollision() {
        when(validator.isUrlValid("https://google.com")).thenReturn(true);
        when(userService.loadUserByUsername("Denys")).thenReturn(creator);
        when(generator.generateUrl()).thenReturn("first111", "second00");
        when(repository.existsByNewUrl("first111")).thenReturn(true);
        when(repository.existsByNewUrl("second00")).thenReturn(false);
        when(repository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlResponse result = service.createShortUrl("https://google.com", "Denys");

        assertEquals("http://localhost:8080/api/v1/urls/second00", result.getNewUrl());
        verify(generator, times(2)).generateUrl();
    }

    @Test
    void shouldThrowWhenUniqueShortUrlCannotBeGenerated() {
        when(generator.generateUrl()).thenReturn("taken123");
        when(repository.existsByNewUrl("taken123")).thenReturn(true);

        assertThrows(ShortUrlGenerationException.class, () -> service.generateUniqueShortUrl());
        verify(generator, times(5)).generateUrl();
    }

    @Test
    void shouldThrowExceptionWhenUrlIsInvalid() {
        when(validator.isUrlValid("invalid-url")).thenReturn(false);

        assertThrows(InvalidUrlException.class,
                () -> service.createShortUrl("invalid-url", "Denys"));

        verifyNoInteractions(generator);
        verifyNoInteractions(userService);
        verifyNoInteractions(repository);
    }

    @Test
    void shouldResolveOriginalUrlAndIncrementTransitionCount() {
        Url url = new Url(1L, "https://google.com", "abc12345", creator,
                LocalDate.now(), LocalDate.now().plusDays(20), 0L);
        when(repository.findByShortUrl("abc12345")).thenReturn(Optional.of(url));

        String result = service.resolveOriginalUrl("abc12345");

        assertEquals("https://google.com", result);
        verify(repository).incrementTransitionCount("abc12345");
    }

    @Test
    void shouldFindByShortUrlAndCreatorName() {
        Url url = new Url("https://google.com", "abc123", creator);
        when(repository.findByShortUrlAndCreatorName("abc123", "Denys"))
                .thenReturn(Optional.of(url));

        Url result = service.findByShortUrlAndCreatorName("abc123", "Denys");

        assertEquals(url, result);
    }

    @Test
    void shouldThrowWhenUrlNotFoundByCreator() {
        when(repository.findByShortUrlAndCreatorName("missing", "Denys"))
                .thenReturn(Optional.empty());

        assertThrows(UrlDoesNotExistException.class,
                () -> service.findByShortUrlAndCreatorName("missing", "Denys"));
    }

    @Test
    void shouldReturnActiveUrlForRedirect() {
        Url url = new Url(1L, "https://google.com", "abc123", creator,
                LocalDate.now(), LocalDate.now().plusDays(5), 3L);

        when(repository.findByShortUrl("abc123")).thenReturn(Optional.of(url));

        Url result = service.findActiveByShortUrl("abc123");

        assertEquals(url, result);
    }

    @Test
    void shouldThrowWhenActiveUrlDoesNotExist() {
        when(repository.findByShortUrl("missing")).thenReturn(Optional.empty());

        assertThrows(UrlDoesNotExistException.class,
                () -> service.findActiveByShortUrl("missing"));
    }

    @Test
    void shouldThrowWhenUrlIsExpired() {
        Url expiredUrl = new Url(1L, "https://google.com", "abc123", creator,
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(10), 3L);

        when(repository.findByShortUrl("abc123")).thenReturn(Optional.of(expiredUrl));

        assertThrows(UrlExpiredException.class,
                () -> service.findActiveByShortUrl("abc123"));
    }

    @Test
    void shouldReturnAllUrlsByCreator() {
        List<Url> urls = List.of(new Url("https://google.com", "abc123", creator));
        when(repository.findAllByCreatorName("Denys")).thenReturn(urls);

        List<UrlResponse> result = service.findAllUrlByCreatorName("Denys");

        assertEquals(1, result.size());
        assertEquals("http://localhost:8080/api/v1/urls/abc123", result.getFirst().getNewUrl());
    }

    @Test
    void shouldReturnActiveUrlsByCreator() {
        List<Url> urls = List.of(new Url("https://google.com", "abc123", creator));
        when(repository.findAllActiveByCreatorName("Denys")).thenReturn(urls);

        List<UrlResponse> result = service.findAllActiveUrlByCreatorName("Denys");

        assertEquals(1, result.size());
        assertEquals("http://localhost:8080/api/v1/urls/abc123", result.getFirst().getNewUrl());
    }

    @Test
    void shouldSaveUrl() {
        Url url = new Url("https://google.com", "abc123", creator);
        when(repository.save(url)).thenReturn(url);

        Url result = service.save(url);

        assertEquals(url, result);
        verify(repository).save(url);
    }

    @Test
    void shouldThrowBlankArgumentWhenSavingIncompleteUrl() {
        Url url = new Url();
        url.setCreator(creator);

        assertThrows(BlankArgumentException.class, () -> service.save(url));
        verifyNoInteractions(repository);
    }

    @Test
    void shouldDeleteUrl() {
        Url stored = new Url("https://google.com", "abc123", creator);

        when(repository.findByShortUrlAndCreatorName("abc123", "Denys"))
                .thenReturn(Optional.of(stored));

        service.deleteByShortUrl("abc123", "Denys");

        verify(repository).delete(stored);
    }

    @Test
    void shouldThrowBlankArgumentWhenDeletingIncompleteUrl() {
        assertThrows(BlankArgumentException.class, () -> service.deleteByShortUrl(null, "Denys"));
        verify(repository, never()).delete(any());
    }

    @Test
    void shouldUpdateUrlWithNewGeneratedShortUrlWithoutChangingCreatedAt() {
        LocalDate createdAt = LocalDate.of(2026, 1, 1);
        Url stored = new Url(1L, "https://google.com", "abc123", creator,
                createdAt, createdAt.plusDays(20), 4L);

        when(repository.findByShortUrlAndCreatorName("abc123", "Denys"))
                .thenReturn(Optional.of(stored));
        when(generator.generateUrl()).thenReturn("newurl99");
        when(repository.existsByNewUrl("newurl99")).thenReturn(false);
        when(repository.save(stored)).thenReturn(stored);

        UrlResponse result = service.regenerateShortUrl("abc123", "Denys");

        assertEquals("http://localhost:8080/api/v1/urls/newurl99", result.getNewUrl());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(createdAt.plusDays(40), result.getExpiredIn());

        verify(repository).save(stored);
    }

    @Test
    void shouldExtendExpirationByTwentyDaysOnRegenerate() {
        LocalDate createdAt = LocalDate.of(2026, 1, 1);
        LocalDate expiredIn = LocalDate.of(2026, 1, 21);
        Url stored = new Url(1L, "https://google.com", "abc123", creator,
                createdAt, expiredIn, 4L);

        when(repository.findByShortUrlAndCreatorName("abc123", "Denys"))
                .thenReturn(Optional.of(stored));
        when(generator.generateUrl()).thenReturn("newurl99");
        when(repository.existsByNewUrl("newurl99")).thenReturn(false);
        when(repository.save(stored)).thenReturn(stored);

        UrlResponse result = service.regenerateShortUrl("abc123", "Denys");

        assertEquals("http://localhost:8080/api/v1/urls/newurl99", result.getNewUrl());
        assertEquals(expiredIn.plusDays(20), result.getExpiredIn());
    }

    @Test
    void shouldRetryRegenerateWhenSaveViolatesUniqueConstraint() {
        Url stored = new Url(1L, "https://google.com", "abc123", creator,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 21), 4L);

        when(repository.findByShortUrlAndCreatorName("abc123", "Denys"))
                .thenReturn(Optional.of(stored));
        when(generator.generateUrl()).thenReturn("first111", "second00");
        when(repository.existsByNewUrl(anyString())).thenReturn(false);
        when(repository.save(stored))
                .thenThrow(new DataIntegrityViolationException("duplicate"))
                .thenReturn(stored);

        UrlResponse result = service.regenerateShortUrl("abc123", "Denys");

        assertEquals("http://localhost:8080/api/v1/urls/second00", result.getNewUrl());
        verify(repository, times(2)).save(stored);
    }

    @Test
    void shouldIncrementTransitionCount() {
        service.incrementTransitionCount("abc123");

        verify(repository).incrementTransitionCount("abc123");
    }

    @Test
    void shouldCheckIfShortUrlExists() {
        when(repository.existsByNewUrl("abc123")).thenReturn(true);

        assertTrue(service.existsByShortUrl("abc123"));
    }
}

