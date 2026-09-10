package com.example.URL_Shortener.programSettings.dto;

import org.hibernate.validator.constraints.URL;

public class InputOriginalUrlDto {

    @URL(message = "URL має починатися з http:// або https://")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
