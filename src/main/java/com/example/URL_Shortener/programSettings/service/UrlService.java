package com.example.URL_Shortener.programSettings.service;

import com.example.URL_Shortener.programSettings.entity.Url;
import com.example.URL_Shortener.programSettings.exception.BlankArgumentException;
import com.example.URL_Shortener.programSettings.exception.UrlDoesNotExistException;
import com.example.URL_Shortener.programSettings.exception.UrlExpiredException;
import com.example.URL_Shortener.programSettings.repository.UrlRepository;
import com.example.URL_Shortener.programSettings.urlUtils.ShortUrlGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UrlService {

    private static final int MAX_GENERATION_ATTEMPTS = 5;

    private final UrlRepository repository;
    private final ShortUrlGenerator generator;

    public UrlService(UrlRepository repository, ShortUrlGenerator generator){
        this.repository = repository;
        this.generator = generator;
    }

    public Url findByShortUrlAndCreatorName(String inputUrl, String creatorName){
        Optional<Url> optionalUrl = repository.findByShortUrlAndCreatorName(inputUrl, creatorName);
        if (!optionalUrl.isPresent()){
            throw new UrlDoesNotExistException();
        }

        return optionalUrl.get();
    }

    public Url findActiveByShortUrl(String shortUrl){
        Url url = repository.findByShortUrl(shortUrl)
                .orElseThrow(UrlDoesNotExistException::new);

        if (url.getExpiredIn() != null && url.getExpiredIn().isBefore(LocalDate.now())){
            throw new UrlExpiredException();
        }

        return url;
    }

    public List<Url> findAllUrlByCreatorName(String creatorName){
        return repository.findAllByCreatorName(creatorName);
    }

    public List<Url> findAllActiveUrlByCreatorName(String creatorName){
        return repository.findAllActiveByCreatorName(creatorName);
    }

    public boolean existsByShortUrl(String shortUrl){
        return repository.existsByNewUrl(shortUrl);
    }

    public Url save(Url url){
        if(url.getCreatorName() == null || url.getOriginalUrl() == null || url.getNewUrl() == null){
            throw new BlankArgumentException();
        }

        return repository.save(url);
    }

    public void delete(Url url){
        if(url.getCreatorName() == null || url.getNewUrl() == null){
            throw new BlankArgumentException();
        }

        Url deletedUrl = findByShortUrlAndCreatorName(url.getNewUrl(), url.getCreatorName());
        repository.delete(deletedUrl);
    }

    public Url update(Url url){
        if(url.getCreatorName() == null || url.getNewUrl() == null){
            throw new BlankArgumentException();
        }

        Url toUpdateUrl = findByShortUrlAndCreatorName(url.getNewUrl(), url.getCreatorName());
        toUpdateUrl.setNewUrl(generateUniqueShortUrl());
        toUpdateUrl.setCreatedAt(LocalDate.now());

        return repository.save(toUpdateUrl);
    }

    private String generateUniqueShortUrl(){
        String candidate;
        int attempts = 0;
        do {
            candidate = generator.generateUrl();
            attempts++;
        } while (existsByShortUrl(candidate) && attempts < MAX_GENERATION_ATTEMPTS);

        return candidate;
    }

    @Transactional
    public void incrementTransitionCount(String shortUrl){
        repository.incrementTransitionCount(shortUrl);
    }
}