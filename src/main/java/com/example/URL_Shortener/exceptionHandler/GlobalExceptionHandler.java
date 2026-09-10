package com.example.URL_Shortener.exceptionHandler;

import com.example.URL_Shortener.programSettings.exception.BlankArgumentException;
import com.example.URL_Shortener.programSettings.exception.InvalidUrlException;
import com.example.URL_Shortener.programSettings.exception.UrlDoesNotExistException;
import com.example.URL_Shortener.programSettings.exception.UrlExpiredException;
import com.example.URL_Shortener.securitySettings.exceptions.InvalidPasswordException;
import com.example.URL_Shortener.securitySettings.exceptions.UserAlreadyExistException;
import com.example.URL_Shortener.securitySettings.exceptions.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BlankArgumentException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, List<String>>> blankArgumentException(BlankArgumentException e){
        return getErrorsMap(e, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(value = InvalidUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, List<String>>> invalidUrlException(InvalidUrlException e){
        return getErrorsMap(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UrlDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, List<String>>> urlDoesNotExistException(UrlDoesNotExistException e){
        return getErrorsMap(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UrlExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ResponseEntity<Map<String, List<String>>> urlExpiredException(UrlExpiredException e){
        return getErrorsMap(e, HttpStatus.GONE);
    }

    @ExceptionHandler(value = InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, List<String>>> invalidPasswordException(InvalidPasswordException e){
        return getErrorsMap(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, List<String>>> userAlreadyExistException(UserAlreadyExistException e){
        return getErrorsMap(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, List<String>>> userNotFoundException(UserNotFoundException e){
        return getErrorsMap(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {BadCredentialsException.class, AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Map<String, List<String>>> authenticationException(AuthenticationException e){
        Map<String, List<String>> errors = new HashMap<>();
        errors.put("errors", Collections.singletonList("Невірне ім'я користувача або пароль!"));
        return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
