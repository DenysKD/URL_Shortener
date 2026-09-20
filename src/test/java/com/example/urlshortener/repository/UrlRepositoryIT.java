package com.example.urlshortener.repository;

import com.example.urlshortener.TestcontainersConfiguration;
import com.example.urlshortener.entity.Role;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

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

    private User createUser(String username) {
        return userRepository.save(new User(username, "encodedPassword", Role.USER));
    }

    @Test
    void shouldSaveAndFindByShortUrl() {
        User creator = createUser("Denys");
        Url url = new Url("https://google.com", "abc123", creator);
        urlRepository.save(url);

        Optional<Url> result = urlRepository.findByShortUrl("abc123");

        assertTrue(result.isPresent());
        assertEquals("https://google.com", result.get().getOriginalUrl());
        assertEquals("Denys", result.get().getCreatorName());
    }

    @Test
    void shouldFindByShortUrlAndCreatorName() {
        User creator = createUser("Denys");
        urlRepository.save(new Url("https://google.com", "abc123", creator));

        Optional<Url> result = urlRepository.findByShortUrlAndCreatorName("abc123", "Denys");

        assertTrue(result.isPresent());
    }

    @Test
    void shouldNotFindByShortUrlAndWrongCreatorName() {
        User creator = createUser("Denys");
        urlRepository.save(new Url("https://google.com", "abc123", creator));

        Optional<Url> result = urlRepository.findByShortUrlAndCreatorName("abc123", "SomeoneElse");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldGenerateCreatedAtAndExpiredInColumns() {
        User creator = createUser("Denys");
        Url saved = urlRepository.save(new Url("https://google.com", "abc123", creator));

        Url reloaded = urlRepository.findById(saved.getId()).orElseThrow();

        assertEquals(LocalDate.now(), reloaded.getCreatedAt());
        assertEquals(LocalDate.now().plusDays(20), reloaded.getExpiredIn());
    }

    @Test
    void shouldDefaultTransitionCountToZero() {
        User creator = createUser("Denys");
        Url saved = urlRepository.save(new Url("https://google.com", "abc123", creator));

        Url reloaded = urlRepository.findById(saved.getId()).orElseThrow();

        assertEquals(0L, reloaded.getTransitionCount());
    }

    @Test
    void shouldIncrementTransitionCount() {
        User creator = createUser("Denys");
        urlRepository.save(new Url("https://google.com", "abc123", creator));

        urlRepository.incrementTransitionCount("abc123");
        urlRepository.incrementTransitionCount("abc123");

        Url result = urlRepository.findByShortUrl("abc123").orElseThrow();
        assertEquals(2L, result.getTransitionCount());
    }

    @Test
    void shouldFindOnlyActiveUrlsForCreator() {
        User creator = createUser("Denys");

        urlRepository.save(new Url("https://google.com", "active01", creator));

        entityManager.getEntityManager().createNativeQuery(
                        "INSERT INTO URL (original_url, new_url, created_at, creator_id, transition_count) " +
                                "VALUES ('https://example.com', 'expired1', :createdAt, :creatorId, 0)")
                .setParameter("createdAt", LocalDate.now().minusDays(30))
                .setParameter("creatorId", creator.getId())
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();

        List<Url> activeUrls = urlRepository.findAllActiveByCreatorName("Denys");

        assertTrue(activeUrls.stream().anyMatch(u -> u.getNewUrl().equals("active01")));
        assertTrue(activeUrls.stream().noneMatch(u -> u.getNewUrl().equals("expired1")));
    }

    @Test
    void shouldEnforceUniqueShortUrl() {
        User creator = createUser("Denys");
        urlRepository.save(new Url("https://google.com", "dup12345", creator));

        assertTrue(urlRepository.existsByNewUrl("dup12345"));
        assertFalse(urlRepository.existsByNewUrl("free12345"));
    }

    @Test
    void shouldFindAllUrlsByCreatorName() {
        User creator = createUser("Denys");
        urlRepository.save(new Url("https://google.com", "first111", creator));
        urlRepository.save(new Url("https://example.com", "second22", creator));

        List<Url> result = urlRepository.findAllByCreatorName("Denys");

        assertEquals(2, result.size());
    }
}
