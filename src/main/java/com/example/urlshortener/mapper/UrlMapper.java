package com.example.urlshortener.mapper;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.Url;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UrlMapper {

    public UrlResponse urlToResponse(Url url){
        return new UrlResponse(url.getId(),
                url.getOriginalUrl(),
                url.getNewUrl(),
                url.getCreatorName(),
                url.getCreatedAt(),
                url.getExpiredIn(),
                url.getTransitionCount());
    }

    public List<UrlResponse> allUrlToAllResponse(Collection<Url> allUrl){
        return allUrl.stream().map(this::urlToResponse).collect(Collectors.toList());
    }
}
