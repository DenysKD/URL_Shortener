package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class InputOriginalUrlDto {

    @NotBlank(message = "URL не може бути порожнім")
    @URL(message = "URL має починатися з http:// або https://")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
