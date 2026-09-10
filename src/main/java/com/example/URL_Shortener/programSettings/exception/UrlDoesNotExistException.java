package com.example.URL_Shortener.programSettings.exception;

public class UrlDoesNotExistException extends RuntimeException {
    private static final String URL_DOES_NOT_EXIST = "Такої URL не існує!";

    public UrlDoesNotExistException(){
        super(URL_DOES_NOT_EXIST);
    }

    public UrlDoesNotExistException(String message) {
        super(message);
    }
}
