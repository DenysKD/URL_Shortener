package com.example.URL_Shortener.programSettingsTests.manager;

import com.example.URL_Shortener.programSettings.dto.UrlDto;
import com.example.URL_Shortener.programSettings.entity.Url;
import com.example.URL_Shortener.programSettings.exception.InvalidUrlException;
import com.example.URL_Shortener.programSettings.manager.UrlManager;
import com.example.URL_Shortener.programSettings.mapper.UrlMapper;
import com.example.URL_Shortener.programSettings.service.UrlService;
import com.example.URL_Shortener.programSettings.urlUtils.ShortUrlGenerator;
import com.example.URL_Shortener.programSettings.validator.UrlValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlManagerTest {

    @Mock
    private UrlService service;

    @Mock
    private ShortUrlGenerator generator;

    @Mock
    private UrlValidator validator;

    @Mock
    private UrlMapper mapper;

    @InjectMocks
    private UrlManager urlManager;


    @Test
    void shouldManageValidUrl() {

        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl("https://google.com");
        urlDto.setCreatorName("Denys");

        Url url = new Url(
                "https://google.com",
                "abc12345",
                "Denys"
        );

        when(validator.isUrlValid("https://google.com"))
                .thenReturn(true);

        when(generator.generateUrl())
                .thenReturn("abc12345");

        when(service.existsByShortUrl("abc12345"))
                .thenReturn(false);

        when(mapper.urlDtoToUrl(urlDto))
                .thenReturn(url);

        when(service.save(url))
                .thenReturn(url);

        String result = urlManager.manageUrl(urlDto);

        assertEquals("abc12345", result);

        verify(validator).isUrlValid("https://google.com");
        verify(generator).generateUrl();
        verify(mapper).urlDtoToUrl(urlDto);
        verify(service).save(url);
    }

    @Test
    void shouldRegenerateShortUrlOnCollision() {

        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl("https://google.com");
        urlDto.setCreatorName("Denys");

        Url savedUrl = new Url(
                "https://google.com",
                "second00",
                "Denys"
        );

        when(validator.isUrlValid("https://google.com")).thenReturn(true);
        when(generator.generateUrl()).thenReturn("first111", "second00");
        when(service.existsByShortUrl("first111")).thenReturn(true);
        when(service.existsByShortUrl("second00")).thenReturn(false);

        when(mapper.urlDtoToUrl(urlDto)).thenReturn(savedUrl);
        when(service.save(savedUrl)).thenReturn(savedUrl);

        String result = urlManager.manageUrl(urlDto);

        assertEquals("second00", result);
        verify(generator, times(2)).generateUrl();
    }

    @Test
    void shouldThrowExceptionWhenUrlIsInvalid() {

        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl("invalid-url");
        urlDto.setCreatorName("Denys");

        when(validator.isUrlValid("invalid-url"))
                .thenReturn(false);

        assertThrows(
                InvalidUrlException.class,
                () -> urlManager.manageUrl(urlDto)
        );

        verify(validator).isUrlValid("invalid-url");

        verifyNoInteractions(generator);
        verifyNoInteractions(mapper);
        verifyNoInteractions(service);
    }

    @Test
    void shouldExtractOriginalUrlAndIncrementTransitionCount() {

        String shortUrl = "abc12345";

        Url url = new Url(
                1L,
                "https://google.com",
                shortUrl,
                "Denys",
                LocalDate.now(),
                LocalDate.now().plusDays(20),
                0L
        );

        when(service.findActiveByShortUrl(shortUrl))
                .thenReturn(url);

        String result = urlManager.extractUrl(shortUrl);

        assertEquals("https://google.com", result);

        verify(service).findActiveByShortUrl(shortUrl);
        verify(service).incrementTransitionCount(shortUrl);
    }
}
