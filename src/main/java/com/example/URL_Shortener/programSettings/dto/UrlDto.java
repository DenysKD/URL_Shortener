package com.example.URL_Shortener.programSettings.dto;

import java.time.LocalDate;

public class UrlDto {

    private Long id;

    private String originalUrl;

    private String newUrl;

    private LocalDate createdAt;

    private String creatorName;

    private LocalDate expiredIn;

    private Long transitionCount;

    public UrlDto(Long id, String originalUrl, String newUrl, String creatorName, LocalDate createdAt, LocalDate expiredIn, Long transitionCount) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.newUrl = newUrl;
        this.creatorName = creatorName;
        this.createdAt = createdAt;
        this.expiredIn = expiredIn;
        this.transitionCount = transitionCount;
    }

    public UrlDto() {
    }

    public Long getTransitionCount() {
        return transitionCount;
    }

    public void setTransitionCount(Long transitionCount) {
        this.transitionCount = transitionCount;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public LocalDate getExpiredIn() {
        return expiredIn;
    }

    public void setExpiredIn(LocalDate expiredIn) {
        this.expiredIn = expiredIn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNewUrl() {
        return newUrl;
    }

    public void setNewUrl(String newUrl) {
        this.newUrl = newUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}
