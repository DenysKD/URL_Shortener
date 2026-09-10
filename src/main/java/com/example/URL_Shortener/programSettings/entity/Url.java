package com.example.URL_Shortener.programSettings.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Table(name = "URL")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url")
    private String originalUrl;

    @Column(name = "new_url")
    private String newUrl;

    @Column(name = "created_at", insertable = false)
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private LocalDate createdAt;

    @Column(name = "creator_name")
    private String creatorName;

    @Column(name = "expired_in", insertable = false, updatable = false)
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private LocalDate expiredIn;

    @Column(name = "transition_count")
    private Long transitionCount = 0L;

    public Url() {
    }

    public Url(String originalUrl, String newUrl, String creatorName) {
        this.originalUrl = originalUrl;
        this.newUrl = newUrl;
        this.creatorName = creatorName;
        this.transitionCount = 0L;
    }

    public Url(Long id, String originalUrl, String newUrl, String creatorName, LocalDate createdAt, LocalDate expiredIn, Long transitionCount) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.newUrl = newUrl;
        this.creatorName = creatorName;
        this.createdAt = createdAt;
        this.expiredIn = expiredIn;
        this.transitionCount = transitionCount;
    }

    public Long getTransitionCount() {
        return transitionCount;
    }

    public void setTransitionCount(Long transitionCount) {
        this.transitionCount = transitionCount;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public void setNewUrl(String newUrl) {
        this.newUrl = newUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public LocalDate getExpiredIn() {
        return expiredIn;
    }

    public Long getId() {
        return id;
    }

    public String getNewUrl() {
        return newUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }
}