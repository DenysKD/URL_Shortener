package com.example.URL_Shortener.securitySettings.exceptions;

public class UserNotFoundException extends RuntimeException {
    private static final String USER_NOT_FOUND = "не вдалося знайти користувача!";
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }
}
