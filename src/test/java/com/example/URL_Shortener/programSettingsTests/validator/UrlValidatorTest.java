package com.example.URL_Shortener.programSettingsTests.validator;

import com.example.URL_Shortener.programSettings.validator.UrlValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlValidatorTest {

    private final UrlValidator validator = new UrlValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "https://google.com",
            "http://example.com",
            "https://example.com/path?query=1",
            "https://sub.example.co.uk:8080/path"
    })
    void shouldAcceptValidHttpUrls(String url) {
        assertTrue(validator.isUrlValid(url));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "not a url",
            "ftp://example.com",
            "javascript:alert(1)",
            "http://",
            "example.com",
            "   "
    })
    void shouldRejectInvalidUrls(String url) {
        assertFalse(validator.isUrlValid(url));
    }

    @Test
    void shouldRejectMalformedUri() {
        assertFalse(validator.isUrlValid("http://[invalid"));
    }
}
