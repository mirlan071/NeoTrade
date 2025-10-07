package com.neotrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neotrade.model.User;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername (String username);
    Optional<User> findByEmail (String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional <User> findPhoneNumber (String phoneNumber);
}
