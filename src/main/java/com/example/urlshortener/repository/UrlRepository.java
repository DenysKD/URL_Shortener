package com.example.urlshortener.repository;

import com.example.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query("SELECT url FROM Url url WHERE url.newUrl = :url AND url.creator.username = :creatorName")
    Optional<Url> findByShortUrlAndCreatorName(@Param("url") String url, @Param("creatorName") String creatorName);

    @Query("SELECT url FROM Url url WHERE url.newUrl = :url")
    Optional<Url> findByShortUrl(@Param("url") String url);

    @Query("SELECT url FROM Url url WHERE url.creator.username = :creatorName")
    List<Url> findAllByCreatorName(@Param("creatorName") String creatorName);

    @Query("SELECT url FROM Url url WHERE url.creator.username = :creatorName AND url.expiredIn >= CURRENT_DATE")
    List<Url> findAllActiveByCreatorName(@Param("creatorName") String creatorName);

    boolean existsByNewUrl(String newUrl);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Url u SET u.transitionCount = u.transitionCount + 1 WHERE u.newUrl = :shortUrl")
    void incrementTransitionCount(@Param("shortUrl") String shortUrl);
}
