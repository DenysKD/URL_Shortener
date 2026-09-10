package com.example.URL_Shortener.securitySettings.exceptions;

public class UserAlreadyExistException extends RuntimeException {
    private static final String USER_ALREADY_EXIST = "Користувач з таким ім'ям уже існує!";

    public UserAlreadyExistException(String message) {
        super(message);
    }

    public UserAlreadyExistException(){
        super(USER_ALREADY_EXIST);
    }
}
