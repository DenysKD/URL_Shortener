package com.example.URL_Shortener.programSettings.exception;

public class InvalidUrlException extends RuntimeException {
    private static final String INVALID_URL = "Введено некоректну URL!";

    public InvalidUrlException() {
        super(INVALID_URL);
    }

    public InvalidUrlException(String message) {
        super(message);
    }
}
