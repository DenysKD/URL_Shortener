package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

public class InputShortUrlDto {

    @NotBlank(message = "Короткий URL не може бути порожнім")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
