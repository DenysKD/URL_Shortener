package com.example.URL_Shortener.programSettings.exception;

public class BlankArgumentException extends RuntimeException {
    private static final String BLANK_ARGUMENT = "Неможливо зберегти URL бо один або декілька аргументів порожні(ім'я користувача, оригінальна URL, коротка URL)!";

    public BlankArgumentException() {
        super(BLANK_ARGUMENT);
    }

    public BlankArgumentException(String message) {
        super(message);
    }
}
