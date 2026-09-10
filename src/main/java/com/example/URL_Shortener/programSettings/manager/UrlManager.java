package com.example.URL_Shortener.programSettings.manager;

import com.example.URL_Shortener.programSettings.dto.UrlDto;
import com.example.URL_Shortener.programSettings.entity.Url;
import com.example.URL_Shortener.programSettings.exception.InvalidUrlException;
import com.example.URL_Shortener.programSettings.mapper.UrlMapper;
import com.example.URL_Shortener.programSettings.service.UrlService;
import com.example.URL_Shortener.programSettings.urlUtils.ShortUrlGenerator;
import com.example.URL_Shortener.programSettings.validator.UrlValidator;
import org.springframework.stereotype.Component;

@Component
public class UrlManager {
    private static final int MAX_GENERATION_ATTEMPTS = 5;

    private final UrlService service;
    private final ShortUrlGenerator generator;
    private final UrlValidator validator;
    private final UrlMapper mapper;

    public UrlManager(ShortUrlGenerator generator, UrlService service, UrlValidator validator, UrlMapper mapper) {
        this.generator = generator;
        this.service = service;
        this.validator = validator;
        this.mapper = mapper;
    }

    public String manageUrl(UrlDto urlDto){
        if(!validator.isUrlValid(urlDto.getOriginalUrl())){
            throw new InvalidUrlException();
        }

        urlDto.setNewUrl(generateUniqueShortUrl());

        Url newUrl = service.save(mapper.urlDtoToUrl(urlDto));

        return newUrl.getNewUrl();
    }

    public String extractUrl(String shortUrl){
        Url url = service.findActiveByShortUrl(shortUrl);
        service.incrementTransitionCount(shortUrl);
        return url.getOriginalUrl();
    }

    private String generateUniqueShortUrl(){
        String candidate;
        int attempts = 0;
        do {
            candidate = generator.generateUrl();
            attempts++;
        } while (service.existsByShortUrl(candidate) && attempts < MAX_GENERATION_ATTEMPTS);

        return candidate;
    }
}
