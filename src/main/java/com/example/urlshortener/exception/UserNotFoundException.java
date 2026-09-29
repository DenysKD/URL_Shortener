package com.example.urlshortener.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {

    private static final String USER_NOT_FOUND = "Не вдалося знайти користувача!";

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }
}
