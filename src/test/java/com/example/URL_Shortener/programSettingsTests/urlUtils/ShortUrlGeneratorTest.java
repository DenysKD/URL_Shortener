package com.example.URL_Shortener.programSettingsTests.urlUtils;

import com.example.URL_Shortener.programSettings.urlUtils.ShortUrlGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

class ShortUrlGeneratorTest {

    private final ShortUrlGenerator generator = new ShortUrlGenerator();

    @RepeatedTest(20)
    void generatedUrlShouldBeBetween6And8CharactersLong() {
        String result = generator.generateUrl();

        assertNotNull(result);
        assertTrue(result.length() >= 6 && result.length() <= 8,
                "Довжина короткого посилання має бути 6-8 символів, отримано: " + result.length());
    }

    @RepeatedTest(20)
    void generatedUrlShouldOnlyContainLettersAndDigits() {
        String result = generator.generateUrl();

        assertTrue(result.matches("[A-Za-z0-9]+"),
                "Коротке посилання має містити лише латинські літери та цифри: " + result);
    }

    @Test
    void generatedUrlsShouldBeReasonablyUnique() {
        String first = generator.generateUrl();
        String second = generator.generateUrl();

        assertNotEquals(first, second);
    }
}
