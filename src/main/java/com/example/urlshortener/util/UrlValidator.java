package com.example.urlshortener.util;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Component
public class UrlValidator {

    public boolean isUrlValid(String url){
        if (url == null || url.isBlank()) {
            return false;
        }

        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();

            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                return false;
            }

            if (uri.getHost() == null || uri.getHost().isBlank()) {
                return false;
            }

            URL parsed = uri.toURL();
            return parsed.getHost() != null && !parsed.getHost().isBlank();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            return false;
        }
    }
}
