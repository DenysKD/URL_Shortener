package com.example.urlshortener.exception;

public class InvalidPasswordException extends RuntimeException {
    private static final String INCORRECT_PASSWORD = "Не коректний пароль! Пароль повинен містити мінімум 8 символів, включаючи латинські великі та маленькі літери та цифри!";

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(){
        super(INCORRECT_PASSWORD);
    }
}
