package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.entity.User;
import com.example.urlshortener.exception.BlankArgumentException;
import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.exception.ShortUrlGenerationException;
import com.example.urlshortener.exception.UrlDoesNotExistException;
import com.example.urlshortener.exception.UrlExpiredException;
import com.example.urlshortener.mapper.UrlMapper;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.util.ShortUrlGenerator;
import com.example.urlshortener.util.UrlValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class UrlService {

    private static final int MAX_GENERATION_ATTEMPTS = 5;

    private final UrlRepository repository;
    private final ShortUrlGenerator generator;
    private final UrlValidator validator;
    private final UrlMapper mapper;
    private final UserService userService;

    public UrlService(UrlRepository repository,
                      ShortUrlGenerator generator,
                      UrlValidator validator,
                      UrlMapper mapper,
                      UserService userService){
        this.repository = repository;
        this.generator = generator;
        this.validator = validator;
        this.mapper = mapper;
        this.userService = userService;
    }

    public UrlResponse createShortUrl(String originalUrl, String username){
        if(!validator.isUrlValid(originalUrl)){
            throw new InvalidUrlException();
        }

        User creator = userService.loadUserByUsername(username);
        Url url = new Url(originalUrl, generateUniqueShortUrl(), creator);
        return mapper.urlToResponse(save(url));
    }

    public String resolveOriginalUrl(String shortUrl){
        Url url = findActiveByShortUrl(shortUrl);
        incrementTransitionCount(shortUrl);
        return url.getOriginalUrl();
    }

    public List<UrlResponse> findAllUrlByCreatorName(String creatorName){
        return mapper.allUrlToAllResponse(repository.findAllByCreatorName(creatorName));
    }

    public List<UrlResponse> findAllActiveUrlByCreatorName(String creatorName){
        return mapper.allUrlToAllResponse(repository.findAllActiveByCreatorName(creatorName));
    }

    public Url findByShortUrlAndCreatorName(String inputUrl, String creatorName){
        return repository.findByShortUrlAndCreatorName(inputUrl, creatorName)
                .orElseThrow(UrlDoesNotExistException::new);
    }

    public Url findActiveByShortUrl(String shortUrl){
        Url url = repository.findByShortUrl(shortUrl)
                .orElseThrow(UrlDoesNotExistException::new);

        if (url.getExpiredIn() != null && url.getExpiredIn().isBefore(LocalDate.now())){
            throw new UrlExpiredException();
        }

        return url;
    }

    public boolean existsByShortUrl(String shortUrl){
        return repository.existsByNewUrl(shortUrl);
    }

    public Url save(Url url){
        if(url.getCreator() == null || url.getOriginalUrl() == null || url.getNewUrl() == null){
            throw new BlankArgumentException();
        }

        return repository.save(url);
    }

    public void deleteByShortUrl(String shortUrl, String username){
        if(shortUrl == null || username == null){
            throw new BlankArgumentException();
        }

        Url deletedUrl = findByShortUrlAndCreatorName(shortUrl, username);
        repository.delete(deletedUrl);
    }

    public UrlResponse regenerateShortUrl(String shortUrl, String username){
        if(shortUrl == null || username == null){
            throw new BlankArgumentException();
        }

        Url toUpdateUrl = findByShortUrlAndCreatorName(shortUrl, username);
        toUpdateUrl.setNewUrl(generateUniqueShortUrl());

        return mapper.urlToResponse(repository.save(toUpdateUrl));
    }

    String generateUniqueShortUrl(){
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String candidate = generator.generateUrl();
            if (!existsByShortUrl(candidate)) {
                return candidate;
            }
        }
        throw new ShortUrlGenerationException();
    }

    @Transactional
    public void incrementTransitionCount(String shortUrl){
        repository.incrementTransitionCount(shortUrl);
    }
}
