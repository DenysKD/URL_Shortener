package com.example.urlshortener.service;

import com.example.urlshortener.entity.Role;
import com.example.urlshortener.entity.User;
import com.example.urlshortener.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSaveUser(){
        User user = new User("Denys", "Password123", Role.USER);

        when(repository.existsByUsername(user.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded_password");

        User savedUser = new User("Denys", "encoded_password", Role.USER);

        when(repository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.save(user);

        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getPassword(), result.getPassword());

        verify(repository).save(any(User.class));
        verify(passwordEncoder).encode("Password123");
    }
}
