package com.example.URL_Shortener.programSettings.exception;

public class UrlExpiredException extends RuntimeException {
    private static final String URL_EXPIRED = "Термін дії цього короткого посилання закінчився!";

    public UrlExpiredException() {
        super(URL_EXPIRED);
    }

    public UrlExpiredException(String message) {
        super(message);
    }
}
