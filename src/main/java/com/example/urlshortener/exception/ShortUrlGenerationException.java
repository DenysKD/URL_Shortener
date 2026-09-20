package com.example.urlshortener.exception;

public class ShortUrlGenerationException extends RuntimeException {
    private static final String MESSAGE = "Не вдалося згенерувати унікальне коротке посилання. Спробуйте ще раз.";

    public ShortUrlGenerationException() {
        super(MESSAGE);
    }
}
