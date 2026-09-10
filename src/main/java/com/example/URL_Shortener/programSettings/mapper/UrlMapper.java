package com.example.URL_Shortener.programSettings.mapper;

import com.example.URL_Shortener.programSettings.dto.InputShortUrlDto;
import com.example.URL_Shortener.programSettings.dto.UrlDto;
import com.example.URL_Shortener.programSettings.dto.InputOriginalUrlDto;
import com.example.URL_Shortener.programSettings.entity.Url;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UrlMapper {

    public UrlDto inputOriginalUrlAndUsernameToUrlDto(InputOriginalUrlDto userInputDto, String username){
        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl(userInputDto.getUrl());
        urlDto.setCreatorName(username);
        return urlDto;
    }

    public UrlDto inputShortlUrlAndUsernameToUrlDto(InputShortUrlDto userInputDto, String username){
        UrlDto urlDto = new UrlDto();
        urlDto.setNewUrl(userInputDto.getUrl());
        urlDto.setCreatorName(username);
        return urlDto;
    }

    public UrlDto inputStringAndUsernameToUrlDto(String shortUrl, String username){
        UrlDto urlDto = new UrlDto();
        urlDto.setNewUrl(shortUrl);
        urlDto.setCreatorName(username);
        return urlDto;
    }

    public Url urlDtoToUrl(UrlDto urlDto){
        Long transitionCount = urlDto.getTransitionCount() != null ? urlDto.getTransitionCount() : 0L;

        return new Url(urlDto.getId(),
                urlDto.getOriginalUrl(),
                urlDto.getNewUrl(),
                urlDto.getCreatorName(),
                urlDto.getCreatedAt(),
                urlDto.getExpiredIn(),
                transitionCount);
    }

    public UrlDto urlToDto(Url url){
        return new UrlDto(url.getId(),
                url.getOriginalUrl(),
                url.getNewUrl(),
                url.getCreatorName(),
                url.getCreatedAt(),
                url.getExpiredIn(),
                url.getTransitionCount());
    }

    public List<UrlDto> allUrlToAllDto(Collection<Url> allUrl){
        return allUrl.stream().map(this::urlToDto).collect(Collectors.toList());
    }
}
