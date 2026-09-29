package com.example.urlshortener.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BlankArgumentException.class)
    public ResponseEntity<Map<String, List<String>>> blankArgumentException(BlankArgumentException e){
        return getErrorsMap(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidUrlException.class)
    public ResponseEntity<Map<String, List<String>>> invalidUrlException(InvalidUrlException e){
        return getErrorsMap(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UrlDoesNotExistException.class)
    public ResponseEntity<Map<String, List<String>>> urlDoesNotExistException(UrlDoesNotExistException e){
        return getErrorsMap(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UrlExpiredException.class)
    public ResponseEntity<Map<String, List<String>>> urlExpiredException(UrlExpiredException e){
        return getErrorsMap(e, HttpStatus.GONE);
    }

    @ExceptionHandler(value = ShortUrlGenerationException.class)
    public ResponseEntity<Map<String, List<String>>> shortUrlGenerationException(ShortUrlGenerationException e){
        return getErrorsMap(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InvalidPasswordException.class)
    public ResponseEntity<Map<String, List<String>>> invalidPasswordException(InvalidPasswordException e){
        return getErrorsMap(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserAlreadyExistException.class)
    public ResponseEntity<Map<String, List<String>>> userAlreadyExistException(UserAlreadyExistException e){
        return getErrorsMap(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<Map<String, List<String>>> authenticationException(AuthenticationException e){
        Map<String, List<String>> errors = new HashMap<>();
        errors.put("errors", Collections.singletonList("Невірне ім'я користувача або пароль!"));
        return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> validationException(MethodArgumentNotValidException e){
        List<String> messages = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " +
                        (fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "некоректне значення"))
                .collect(Collectors.toList());

        Map<String, List<String>> errors = new HashMap<>();
        errors.put("errors", messages);
        return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, List<String>>> getErrorsMap(Throwable e, HttpStatus status){
        Map<String, List<String>> errors = new HashMap<>();
        errors.put("errors", Collections.singletonList(e.getMessage()));
        return new ResponseEntity<>(errors, new HttpHeaders(), status);
    }
}
