package com.example.urlshortener.service;

import com.example.urlshortener.entity.User;
import com.example.urlshortener.exception.UserAlreadyExistException;
import com.example.urlshortener.exception.UserNotFoundException;
import com.example.urlshortener.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = repository.findByUsername(username);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        } else {
            throw new UserNotFoundException();
        }
    }

    public User save(User user){
        if(repository.existsByUsername(user.getUsername())){
            throw new UserAlreadyExistException();
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        try {
            return repository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException();
        }
    }
}
