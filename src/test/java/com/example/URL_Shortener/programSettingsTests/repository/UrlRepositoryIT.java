package com.example.URL_Shortener.programSettingsTests.repository;

import com.example.URL_Shortener.programSettings.entity.Url;
import com.example.URL_Shortener.programSettings.repository.UrlRepository;
import com.example.URL_Shortener.securitySettings.entity.Role;
import com.example.URL_Shortener.securitySettings.entity.User;
import com.example.URL_Shortener.securitySettings.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import com.example.URL_Shortener.TestcontainersConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class UrlRepositoryIT {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private void createUser(String username) {
        userRepository.save(new User(username, "encodedPassword", Role.USER));
    }

    @Test
    void shouldSaveAndFindByShortUrl() {
        createUser("Denys");
        Url url = new Url("https://google.com", "abc123", "Denys");
        urlRepository.save(url);

        Optional<Url> result = urlRepository.findByShortUrl("abc123");

        assertTrue(result.isPresent());
        assertEquals("https://google.com", result.get().getOriginalUrl());
    }

    @Test
    void shouldFindByShortUrlAndCreatorName() {
        createUser("Denys");
        urlRepository.save(new Url("https://google.com", "abc123", "Denys"));

        Optional<Url> result = urlRepository.findByShortUrlAndCreatorName("abc123", "Denys");

        assertTrue(result.isPresent());
    }

    @Test
    void shouldNotFindByShortUrlAndWrongCreatorName() {
        createUser("Denys");
        urlRepository.save(new Url("https://google.com", "abc123", "Denys"));

        Optional<Url> result = urlRepository.findByShortUrlAndCreatorName("abc123", "SomeoneElse");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldGenerateCreatedAtAndExpiredInColumns() {
        createUser("Denys");
        Url saved = urlRepository.save(new Url("https://google.com", "abc123", "Denys"));

        Url reloaded = urlRepository.findById(saved.getId()).orElseThrow();

        assertEquals(LocalDate.now(), reloaded.getCreatedAt());
        assertEquals(LocalDate.now().plusDays(20), reloaded.getExpiredIn());
    }

    @Test
    void shouldDefaultTransitionCountToZero() {
        createUser("Denys");
        Url saved = urlRepository.save(new Url("https://google.com", "abc123", "Denys"));

        Url reloaded = urlRepository.findById(saved.getId()).orElseThrow();

        assertEquals(0L, reloaded.getTransitionCount());
    }

    @Test
    void shouldIncrementTransitionCount() {
        createUser("Denys");
        urlRepository.save(new Url("https://google.com", "abc123", "Denys"));

        urlRepository.incrementTransitionCount("abc123");
        urlRepository.incrementTransitionCount("abc123");

        Url result = urlRepository.findByShortUrl("abc123").orElseThrow();
        assertEquals(2L, result.getTransitionCount());
    }

    @Test
    void shouldFindOnlyActiveUrlsForCreator() {
        createUser("Denys");

        urlRepository.save(new Url("https://google.com", "active01", "Denys"));

        entityManager.getEntityManager().createNativeQuery(
                        "INSERT INTO URL (original_url, new_url, created_at, creator_name, transition_count) " +
                                "VALUES ('https://example.com', 'expired1', :createdAt, 'Denys', 0)")
                .setParameter("createdAt", LocalDate.now().minusDays(30))
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();

        List<Url> activeUrls = urlRepository.findAllActiveByCreatorName("Denys");

        assertTrue(activeUrls.stream().anyMatch(u -> u.getNewUrl().equals("active01")));
        assertTrue(activeUrls.stream().noneMatch(u -> u.getNewUrl().equals("expired1")));
    }

    @Test
    void shouldEnforceUniqueShortUrl() {
        createUser("Denys");
        urlRepository.save(new Url("https://google.com", "dup12345", "Denys"));

        assertTrue(urlRepository.existsByNewUrl("dup12345"));
        assertFalse(urlRepository.existsByNewUrl("free12345"));
    }

    @Test
    void shouldFindAllUrlsByCreatorName() {
        createUser("Denys");
        urlRepository.save(new Url("https://google.com", "first111", "Denys"));
        urlRepository.save(new Url("https://example.com", "second22", "Denys"));

        List<Url> result = urlRepository.findAllByCreatorName("Denys");

        assertEquals(2, result.size());
    }
}