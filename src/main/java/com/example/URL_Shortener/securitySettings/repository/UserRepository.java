package com.example.URL_Shortener.securitySettings.repository;

import com.example.URL_Shortener.securitySettings.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    public Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    public boolean existsByUsername(@Param("username") String username);
}
