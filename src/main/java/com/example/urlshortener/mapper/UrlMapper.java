package com.example.urlshortener.mapper;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UrlMapper {

    private static final String REDIRECT_PATH = "/api/v1/urls/";

    private final String baseUrl;

    public UrlMapper(@Value("${application.base-url}") String baseUrl){
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public UrlResponse urlToResponse(Url url){
        return new UrlResponse(url.getId(),
                url.getOriginalUrl(),
                baseUrl + REDIRECT_PATH + url.getNewUrl(),
                url.getCreatorName(),
                url.getCreatedAt(),
                url.getExpiredIn(),
                url.getTransitionCount());
    }

    public List<UrlResponse> allUrlToAllResponse(Collection<Url> allUrl){
        return allUrl.stream().map(this::urlToResponse).collect(Collectors.toList());
    }
}
