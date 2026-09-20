package com.example.urlshortener.exception;

public class UrlExpiredException extends RuntimeException {
    private static final String URL_EXPIRED = "Термін дії цього короткого посилання закінчився!";

    public UrlExpiredException() {
        super(URL_EXPIRED);
    }

    public UrlExpiredException(String message) {
        super(message);
    }
}
