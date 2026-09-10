package com.example.URL_Shortener.programSettingsTests.service;

import com.example.URL_Shortener.programSettings.entity.Url;
import com.example.URL_Shortener.programSettings.exception.BlankArgumentException;
import com.example.URL_Shortener.programSettings.exception.UrlDoesNotExistException;
import com.example.URL_Shortener.programSettings.exception.UrlExpiredException;
import com.example.URL_Shortener.programSettings.repository.UrlRepository;
import com.example.URL_Shortener.programSettings.service.UrlService;
import com.example.URL_Shortener.programSettings.urlUtils.ShortUrlGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private UrlService service;

    @Test
    void shouldFindByShortUrlAndCreatorName() {
        Url url = new Url("https://google.com", "abc123", "Denys");
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
        Url url = new Url(1L, "https://google.com", "abc123", "Denys",
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
        Url expiredUrl = new Url(1L, "https://google.com", "abc123", "Denys",
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(10), 3L);

        when(repository.findByShortUrl("abc123")).thenReturn(Optional.of(expiredUrl));

        assertThrows(UrlExpiredException.class,
                () -> service.findActiveByShortUrl("abc123"));
    }

    @Test
    void shouldReturnAllUrlsByCreator() {
        List<Url> urls = List.of(new Url("https://google.com", "abc123", "Denys"));
        when(repository.findAllByCreatorName("Denys")).thenReturn(urls);

        assertEquals(urls, service.findAllUrlByCreatorName("Denys"));
    }

    @Test
    void shouldReturnActiveUrlsByCreator() {
        List<Url> urls = List.of(new Url("https://google.com", "abc123", "Denys"));
        when(repository.findAllActiveByCreatorName("Denys")).thenReturn(urls);

        assertEquals(urls, service.findAllActiveUrlByCreatorName("Denys"));
    }

    @Test
    void shouldSaveUrl() {
        Url url = new Url("https://google.com", "abc123", "Denys");
        when(repository.save(url)).thenReturn(url);

        Url result = service.save(url);

        assertEquals(url, result);
        verify(repository).save(url);
    }

    @Test
    void shouldThrowBlankArgumentWhenSavingIncompleteUrl() {
        Url url = new Url();
        url.setCreatorName("Denys");

        assertThrows(BlankArgumentException.class, () -> service.save(url));
        verifyNoInteractions(repository);
    }

    @Test
    void shouldDeleteUrl() {
        Url stored = new Url("https://google.com", "abc123", "Denys");
        Url toDelete = new Url();
        toDelete.setCreatorName("Denys");
        toDelete.setNewUrl("abc123");

        when(repository.findByShortUrlAndCreatorName("abc123", "Denys"))
                .thenReturn(Optional.of(stored));

        service.delete(toDelete);

        verify(repository).delete(stored);
    }

    @Test
    void shouldThrowBlankArgumentWhenDeletingIncompleteUrl() {
        Url toDelete = new Url();
        toDelete.setCreatorName("Denys");

        assertThrows(BlankArgumentException.class, () -> service.delete(toDelete));
        verify(repository, never()).delete(any());
    }

    @Test
    void shouldUpdateUrlWithNewGeneratedShortUrl() {
        Url stored = new Url("https://google.com", "abc123", "Denys");
        Url toUpdate = new Url();
        toUpdate.setCreatorName("Denys");
        toUpdate.setNewUrl("abc123");

        when(repository.findByShortUrlAndCreatorName("abc123", "Denys"))
                .thenReturn(Optional.of(stored));
        when(generator.generateUrl()).thenReturn("newurl99");
        when(repository.save(stored)).thenReturn(stored);

        Url result = service.update(toUpdate);

        assertEquals("newurl99", result.getNewUrl());
        verify(repository).save(stored);
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
