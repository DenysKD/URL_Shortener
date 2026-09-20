package com.example.urlshortener.entity;

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

    @Column(name = "created_at", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private LocalDate createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "expired_in", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private LocalDate expiredIn;

    @Column(name = "transition_count")
    private Long transitionCount = 0L;

    public Url() {
    }

    public Url(String originalUrl, String newUrl, User creator) {
        this.originalUrl = originalUrl;
        this.newUrl = newUrl;
        this.creator = creator;
        this.transitionCount = 0L;
    }

    public Url(Long id, String originalUrl, String newUrl, User creator, LocalDate createdAt, LocalDate expiredIn, Long transitionCount) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.newUrl = newUrl;
        this.creator = creator;
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

    public void setCreator(User creator) {
        this.creator = creator;
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

    public User getCreator() {
        return creator;
    }

    public String getCreatorName() {
        return creator != null ? creator.getUsername() : null;
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
