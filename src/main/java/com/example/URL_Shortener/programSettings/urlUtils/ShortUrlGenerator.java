package com.example.URL_Shortener.programSettings.urlUtils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortUrlGenerator {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 8;

    private final SecureRandom random = new SecureRandom();

    public String generateUrl() {
        int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH + 1);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

}
